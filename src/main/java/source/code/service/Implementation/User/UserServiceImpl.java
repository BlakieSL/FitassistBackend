package source.code.service.Implementation.User;

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
import source.code.event.events.User.UserDeleteEvent;
import source.code.event.events.User.UserRegisterEvent;
import source.code.event.events.User.UserUpdateEvent;
import source.code.dto.Other.UserCredentialsDto;
import source.code.dto.Request.UserCreateDto;
import source.code.dto.Request.UserUpdateDto;
import source.code.dto.Response.User.UserResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.CacheNames;
import source.code.helper.UserDetailsHelper;
import source.code.mapper.UserMapper;
import source.code.model.User.User;
import source.code.repository.UserRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.User.UserService;
import source.code.service.Implementation.Helpers.JsonPatchServiceImpl;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RepositoryHelper repositoryHelper;
  private final UserRepository userRepository;

  public UserServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                         UserRepository userRepository,
                         UserMapper userMapper,
                         ValidationService validationService,
                         JsonPatchServiceImpl jsonPatchService,
                         PasswordEncoder passwordEncoder,
                         RepositoryHelper repositoryHelper) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.passwordEncoder = passwordEncoder;
    this.repositoryHelper = repositoryHelper;
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
    User user = find(id);
    userRepository.delete(user);

    applicationEventPublisher.publishEvent(new UserDeleteEvent(this, user));
  }

  @Transactional
  public void updateUser(int userId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    User user = find(userId);
    UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch, userId);

    validatePasswordIfNeeded(user, patchedUserUpdateDto);
    validationService.validate(patchedUserUpdateDto);

    userMapper.updateUserFromDto(user, patchedUserUpdateDto);
    User savedUser = userRepository.save(user);

    applicationEventPublisher.publishEvent(new UserUpdateEvent(this, savedUser));
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

  @Cacheable(value = CacheNames.USER_DETAILS, key = "#username")
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findUserCredentialsByEmail(username)
            .map(UserDetailsHelper::buildUserDetails)
            .orElseThrow(() -> new RecordNotFoundException(User.class, username));
  }

  private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
    return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
  }

  @Cacheable(value = CacheNames.USER_BY_ID, key = "#userId")
  public UserResponseDto getUser(int userId) {
    User user = find(userId);
    return userMapper.toResponse(user);
  }

  @Cacheable(value = CacheNames.USER_ID_BY_EMAIL, key = "#email")
  public int getUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
            .map(User::getId)
            .orElseThrow(() -> new RecordNotFoundException(User.class, email));
  }

  private User find(int userId) {
    return repositoryHelper.find(userRepository, User.class, userId);
  }
}
