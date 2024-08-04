package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.helper.CalculationsHelper;
import com.example.simplefullstackproject.helper.JsonPatchHelper;
import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.UserMapper;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.repository.ExerciseRepository;
import com.example.simplefullstackproject.repository.RoleRepository;
import com.example.simplefullstackproject.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ExerciseRepository exerciseRepository;
    private final JsonPatchHelper jsonPatchHelper;

    public UserService(UserRepository userRepository,
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

    private Optional<UserDto> findUserCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDetails);
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
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id " + id + " not found"));
        return userMapper.toResponse(user);
    }

    public Integer getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with email: " + email + " not found"));
    }

    @Transactional
    public UserResponse register(UserAdditionDto request) {
        validationHelper.validate(request);

        User user = userMapper.toEntity(request);
        userMapper.setCalculatedCalories(user,request);
        userMapper.addDefaultRole(user);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException(
                                "User with id: " + id + " not found"));
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

        userMapper.updateUserFromDto(user, patchedUserUpdateRequest);
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
