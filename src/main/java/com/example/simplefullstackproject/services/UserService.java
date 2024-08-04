package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.components.JsonPatchHelper;
import com.example.simplefullstackproject.dtos.*;
import com.example.simplefullstackproject.models.Role;
import com.example.simplefullstackproject.models.User;
import com.example.simplefullstackproject.repositories.ExerciseRepository;
import com.example.simplefullstackproject.repositories.RoleRepository;
import com.example.simplefullstackproject.repositories.UserRepository;
import com.example.simplefullstackproject.services.Mappers.UserDtoMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ExerciseRepository exerciseRepository;
    private final JsonPatchHelper jsonPatchHelper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserDtoMapper userDtoMapper,
                       PasswordEncoder passwordEncoder,
                       ExerciseRepository exerciseRepository,
                       ValidationHelper validationHelper,
                       CalculationsHelper calculationsHelper,
                       JsonPatchHelper jsonPatchHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userDtoMapper = userDtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.exerciseRepository = exerciseRepository;
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    private Optional<UserDto> findUserCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDtoMapper::mapDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserCredentialsByEmail(username)
                .map(dto -> org.springframework.security.core.userdetails.User.builder()
                        .username(dto.getEmail())
                        .password(dto.getPassword())
                        .roles(dto.getRoles().toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        return userDtoMapper.map(user);
    }

    public Integer getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("User with email: " + email + " not found"));
    }

    @Transactional
    public UserResponse register(UserRequest request) {
        validationHelper.validate(request);

        User user = userDtoMapper.map(request);

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();

        user.setCalculatedCalories(calculationsHelper.calculateCaloricNeeds(
                user.getWeight(),
                user.getHeight(),
                age,
                user.getGender(),
                user.getActivityLevel(),
                user.getGoal()));

        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        return userDtoMapper.map(savedUser);
    }

    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with id: " + id + "not found");
        }
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException());
        user.getRoles().clear();
        userRepository.delete(user);
    }

    @Transactional
    public void modifyUser(Integer userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found"));

        UserResponse userDto = getUserById(userId);
        UserUpdateRequest patchedUserUpdateRequest = jsonPatchHelper.applyPatch(patch, userDto, UserUpdateRequest.class);

        validationHelper.validate(patchedUserUpdateRequest);

        updateUserFields(user, patchedUserUpdateRequest);

        int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
        user.setCalculatedCalories(calculationsHelper.calculateCaloricNeeds(
                user.getWeight(),
                user.getHeight(),
                age,
                user.getGender(),
                patchedUserUpdateRequest.getActivityLevel(),
                patchedUserUpdateRequest.getGoal()
        ));

        userRepository.save(user);
    }

    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.getHeight() != user.getHeight()) {
            user.setHeight(request.getHeight());
        }
        if (request.getWeight() != user.getWeight()) {
            user.setWeight(request.getWeight());
        }
    }
}
