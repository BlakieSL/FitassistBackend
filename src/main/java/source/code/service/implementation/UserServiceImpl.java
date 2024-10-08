package source.code.service.implementation;

import source.code.dto.request.UserCreateDto;
import source.code.dto.other.UserCredentialsDto;
import source.code.dto.response.UserResponseDto;
import source.code.dto.request.UserUpdateDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.UserMapper;
import source.code.model.User;
import source.code.repository.ExerciseRepository;
import source.code.repository.RoleRepository;
import source.code.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import source.code.service.declaration.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ExerciseRepository exerciseRepository;
    private final JsonPatchHelper jsonPatchHelper;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           ExerciseRepository exerciseRepository,
                           ValidationHelper validationHelper,
                           CalculationsHelper calculationsHelper,
                           JsonPatchHelper jsonPatchHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.exerciseRepository = exerciseRepository;
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    @Transactional
    public UserResponseDto register(UserCreateDto request) {
        validationHelper.validate(request);

        User user = userMapper.toEntity(request);
        userMapper.setCalculatedCalories(user,request);
        userMapper.addDefaultRole(user);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + id + " not found"));
        user.getRoles().clear();
        userRepository.delete(user);
    }

    @Transactional
    public void updateUser(int userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found"));

        UserResponseDto userDto = getUser(userId);
        UserUpdateDto patchedUserUpdateDto = jsonPatchHelper.applyPatch(patch, userDto, UserUpdateDto.class);

        if(patchedUserUpdateDto.getOldPassword() != null && patchedUserUpdateDto.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(patchedUserUpdateDto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Old password does not match");
            }
        }

        validationHelper.validate(patchedUserUpdateDto);

        userMapper.updateUserFromDto(user, patchedUserUpdateDto);
        userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserCredentialsByEmail(username)
                .map(dto -> org.springframework.security.core.userdetails.User.builder()
                        .username(dto.getEmail())
                        .password(dto.getPassword())
                        .roles(dto.getRoles().toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDetails);
    }

    public UserResponseDto getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id " + id + " not found"));
        return userMapper.toResponse(user);
    }

    public int getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with email: " + email + " not found"));
    }
}
