package source.code.service.implementation.user;

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
import source.code.dto.pojo.UserCredentialsDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.request.user.UserUpdateDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.event.events.User.UserDeleteEvent;
import source.code.event.events.User.UserRegisterEvent;
import source.code.event.events.User.UserUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.user.UserDetailsHelper;
import source.code.mapper.UserMapper;
import source.code.model.user.profile.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.user.UserService;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;

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

    @Override
    @Transactional
    public UserResponseDto register(UserCreateDto request) {
        User user = userMapper.toEntity(request);
        userMapper.setCalculatedCalories(user, request);
        userMapper.addDefaultRole(user);

        User savedUser = userRepository.save(user);
        applicationEventPublisher.publishEvent(UserRegisterEvent.of(this, request));

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        User user = find(id);
        userRepository.delete(user);

        applicationEventPublisher.publishEvent(UserDeleteEvent.of(this, user));
    }

    @Override
    @Transactional
    public void updateUser(int userId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        User user = find(userId);
        UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch, userId);

        validatePasswordIfNeeded(user, patchedUserUpdateDto);
        validationService.validate(patchedUserUpdateDto);
        userMapper.updateUserFromDto(user, patchedUserUpdateDto);
        User savedUser = userRepository.save(user);

        applicationEventPublisher.publishEvent(UserUpdateEvent.of(this, savedUser));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserCredentialsByEmail(username)
                .map(UserDetailsHelper::buildUserDetails)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, username));
    }

    private UserUpdateDto applyPatchToUser(JsonMergePatch patch, int userId)
            throws JsonPatchException, JsonProcessingException
    {
        UserResponseDto userDto = getUser(userId);
        return jsonPatchService.applyPatch(patch, userDto, UserUpdateDto.class);
    }

    @Override
    @Cacheable(value = CacheNames.USER_BY_ID, key = "#userId")
    public UserResponseDto getUser(int userId) {
        User user = find(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = CacheNames.USER_ID_BY_EMAIL, key = "#email")
    public int getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, email));
    }

    private void validatePasswordIfNeeded(User user, UserUpdateDto dto) {
        if (isPasswordChangeRequested(dto)) {
            validateOldPassword(user, dto.getOldPassword());
        }
    }

    private boolean isPasswordChangeRequested(UserUpdateDto dto) {
        if (dto.getOldPassword() == null && dto.getPassword() != null) {
            throw new IllegalArgumentException(
                    "Old password is required when changing to a new password."
            );
        }
        return dto.getOldPassword() != null && dto.getPassword() != null;
    }


    private void validateOldPassword(User user, String oldPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
    }

    private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
        return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
    }

    private User find(int userId) {
        return repositoryHelper.find(userRepository, User.class, userId);
    }
}
