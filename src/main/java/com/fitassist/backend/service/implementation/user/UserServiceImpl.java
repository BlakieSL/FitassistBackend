package com.fitassist.backend.service.implementation.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.UserDetailsHelper;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.UserCredentialsDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.UserMapper;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.user.UserService;
import com.fitassist.backend.service.implementation.helpers.JsonPatchServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	private final RepositoryHelper repositoryHelper;

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ValidationService validationService,
			JsonPatchServiceImpl jsonPatchService, PasswordEncoder passwordEncoder, RepositoryHelper repositoryHelper) {
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
		userMapper.addDefaultRole(user);

		User savedUser = userRepository.save(user);

		return userMapper.toResponse(savedUser);
	}

	@Override
	@Transactional
	public void deleteUser(int id) {
		User user = find(id);
		userRepository.delete(user);
	}

	@Override
	@Transactional
	public void updateUser(int userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		User user = find(userId);
		UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch, userId);

		validatePasswordIfNeeded(user, patchedUserUpdateDto);
		validationService.validate(patchedUserUpdateDto);
		userMapper.updateUserFromDto(user, patchedUserUpdateDto);
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateUserSimple(int userId, UserUpdateDto updateDto) {
		User user = find(userId);

		validatePasswordIfNeeded(user, updateDto);
		validationService.validate(updateDto);
		userMapper.updateUserFromDto(user, updateDto);
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findUserCredentialsByEmail(username).map(UserDetailsHelper::buildUserDetails)
			.orElseThrow(() -> RecordNotFoundException.of(User.class, username));
	}

	@Override
	public UserResponseDto getUser(int userId) {
		User user = find(userId);
		return userMapper.toResponse(user);
	}

	@Override
	public AuthorDto getPublicUser(int userId) {
		User user = find(userId);
		return userMapper.toAuthorDto(user);
	}

	@Override
	public int getUserIdByEmail(String email) {
		return userRepository.findByEmail(email)
			.map(User::getId)
			.orElseThrow(() -> RecordNotFoundException.of(User.class, email));
	}

	private UserUpdateDto applyPatchToUser(JsonMergePatch patch, int userId)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, UserUpdateDto.class);
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

	private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
		return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
	}

	private User find(int userId) {
		return repositoryHelper.find(userRepository, User.class, userId);
	}

}
