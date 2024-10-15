package source.code.service.implementation.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import source.code.dto.other.UserCredentialsDto;
import source.code.dto.request.UserCreateDto;
import source.code.dto.request.UserUpdateDto;
import source.code.dto.response.UserResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.UserDetailsHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.UserMapper;
import source.code.model.User.User;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository,
                         UserMapper userMapper,
                         ValidationHelper validationHelper,
                         JsonPatchHelper jsonPatchHelper,
                         PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserResponseDto register(UserCreateDto request) {
    User user = userMapper.toEntity(request);
    userMapper.setCalculatedCalories(user, request);
    userMapper.addDefaultRole(user);

    User savedUser = userRepository.save(user);

    return userMapper.toResponse(savedUser);
  }

  @Transactional
  public void deleteUser(int id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + id + " not found"));
    userRepository.delete(user);
  }

  @Transactional
  public void updateUser(int userId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    User user = getUserOrThrow(userId);

    UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch, userId);

    validatePasswordIfNeeded(user, patchedUserUpdateDto);

    validationHelper.validate(patchedUserUpdateDto);

    userMapper.updateUserFromDto(user, patchedUserUpdateDto);
    userRepository.save(user);
  }

  private User getUserOrThrow(int userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));
  }

  private UserUpdateDto applyPatchToUser(JsonMergePatch patch, int userId)
          throws JsonPatchException, JsonProcessingException {

    UserResponseDto userDto = getUser(userId);
    return jsonPatchHelper.applyPatch(patch, userDto, UserUpdateDto.class);
  }

  private void validatePasswordIfNeeded(User user, UserUpdateDto dto) {
    if (isPasswordChangeRequested(dto)) {
      validateOldPassword(user, dto.getOldPassword());
    }
  }

  private boolean isPasswordChangeRequested(UserUpdateDto dto) {
    return dto.getOldPassword() != null && dto.getPassword() != null;
  }

  private void validateOldPassword(User user, String oldPassword) {
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Old password does not match");
    }
  }


  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findUserCredentialsByEmail(username)
            .map(UserDetailsHelper::buildUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(
                    "User " + username + " not found"));
  }

  private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
    return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
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
