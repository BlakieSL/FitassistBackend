package source.code.service.implementation.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import source.code.cache.event.User.UserDeleteEvent;
import source.code.cache.event.User.UserRegisterEvent;
import source.code.cache.event.User.UserUpdateEvent;
import source.code.dto.other.UserCredentialsDto;
import source.code.dto.request.UserCreateDto;
import source.code.dto.request.UserUpdateDto;
import source.code.dto.response.UserResponseDto;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.helper.UserDetailsHelper;
import source.code.mapper.UserMapper;
import source.code.model.User.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.User.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public UserServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                         UserRepository userRepository,
                         UserMapper userMapper,
                         ValidationService validationService,
                         JsonPatchServiceImpl jsonPatchService,
                         PasswordEncoder passwordEncoder) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserResponseDto register(UserCreateDto request) {
    User user = userMapper.toEntity(request);
    userMapper.setCalculatedCalories(user, request);
    userMapper.addDefaultRole(user);

    User savedUser = userRepository.save(user);

    applicationEventPublisher.publishEvent(new UserRegisterEvent(this, request));

    return userMapper.toResponse(savedUser);
  }

  @Transactional
  public void deleteUser(int id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + id + " not found"));

    userRepository.delete(user);

    applicationEventPublisher.publishEvent(new UserDeleteEvent(this, user));
  }

  @Transactional
  public void updateUser(int userId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    User user = getUserOrThrow(userId);

    UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch, userId);

    validatePasswordIfNeeded(user, patchedUserUpdateDto);

    validationService.validate(patchedUserUpdateDto);

    userMapper.updateUserFromDto(user, patchedUserUpdateDto);
    User savedUser = userRepository.save(user);

    applicationEventPublisher.publishEvent(new UserUpdateEvent(this, savedUser));
  }

  private User getUserOrThrow(int userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));
  }

  private UserUpdateDto applyPatchToUser(JsonMergePatch patch, int userId)
          throws JsonPatchException, JsonProcessingException {

    UserResponseDto userDto = getUser(userId);
    return jsonPatchService.applyPatch(patch, userDto, UserUpdateDto.class);
  }

  private void validatePasswordIfNeeded(User user, UserUpdateDto dto) {
    if (isPasswordChangeRequested(dto)) {
      validateOldPassword(user, dto.getOldPassword());
    }
  }

  private boolean isPasswordChangeRequested(UserUpdateDto dto) {
    if (dto.getOldPassword() == null && dto.getPassword() != null) {
      throw new IllegalArgumentException("Old password is required when changing to a new password.");
    }
    return dto.getOldPassword() != null && dto.getPassword() != null;
  }


  private void validateOldPassword(User user, String oldPassword) {
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Old password does not match");
    }
  }

  @Cacheable(value = {"userDetails"}, key = "#username")
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findUserCredentialsByEmail(username)
            .map(UserDetailsHelper::buildUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(
                    "User " + username + " not found"));
  }

  private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
    return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
  }

  @Cacheable(value = {"userById"}, key = "#userId")
  public UserResponseDto getUser(int userId) {
    User user = getUserOrThrow(userId);

    return userMapper.toResponse(user);
  }

  @Cacheable(value = {"userIdByEmail"}, key = "#email")
  public int getUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
            .map(User::getId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with email: " + email + " not found"));
  }
}
