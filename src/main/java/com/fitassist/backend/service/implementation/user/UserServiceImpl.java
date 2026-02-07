package com.fitassist.backend.service.implementation.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.UserDetailsBuilder;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.UserCredentialsDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.UserMapper;
import com.fitassist.backend.model.user.Role;
import com.fitassist.backend.model.user.RoleEnum;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.RoleRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.user.UserPopulationService;
import com.fitassist.backend.service.declaration.user.UserService;
import com.fitassist.backend.service.implementation.helpers.JsonPatchServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	private final RepositoryHelper repositoryHelper;

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final CalculationsService calculationsService;

	private final UserPopulationService userPopulationService;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ValidationService validationService,
			JsonPatchServiceImpl jsonPatchService, PasswordEncoder passwordEncoder, RepositoryHelper repositoryHelper,
			RoleRepository roleRepository, CalculationsService calculationsService,
			UserPopulationService userPopulationService) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.passwordEncoder = passwordEncoder;
		this.repositoryHelper = repositoryHelper;
		this.roleRepository = roleRepository;
		this.calculationsService = calculationsService;
		this.userPopulationService = userPopulationService;
	}

	@Override
	@Transactional
	public UserResponseDto register(UserCreateDto request) {
		User user = userMapper.toEntity(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		addDefaultRole(user);

		User savedUser = userRepository.save(user);
		UserResponseDto response = userMapper.toResponse(savedUser);
		userPopulationService.populate(response);

		return calculateCalories(savedUser, response);
	}

	private UserResponseDto calculateCalories(User user, UserResponseDto response) {
		if (hasRequiredData(user)) {
			int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
			BigDecimal calories = calculationsService.calculateCaloricNeeds(user.getWeight(), user.getHeight(), age,
					user.getGender(), user.getActivityLevel(), user.getGoal());
			response.setCalculatedCalories(calories);
		}
		return response;
	}

	private boolean hasRequiredData(User user) {
		return user.getWeight() != null && user.getHeight() != null && user.getActivityLevel() != null
				&& user.getGoal() != null && user.getBirthday() != null && user.getGender() != null;
	}

	private void addDefaultRole(User user) {
		Role role = roleRepository.findByName(RoleEnum.USER)
			.orElseThrow(() -> new RecordNotFoundException(Role.class, RoleEnum.USER.name()));
		user.getRoles().add(role);
	}

	@Override
	@Transactional
	public void deleteUser(int id) {
		User user = find(id);
		userRepository.delete(user);
	}

	private User find(int userId) {
		return repositoryHelper.find(userRepository, User.class, userId);
	}

	@Override
	@Transactional
	public void updateUser(int userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		User user = find(userId);
		UserUpdateDto patchedUserUpdateDto = applyPatchToUser(patch);

		validatePasswordIfPresent(user, patchedUserUpdateDto);
		validationService.validate(patchedUserUpdateDto);
		userMapper.update(user, patchedUserUpdateDto);
		hashPasswordIfPresent(user, patchedUserUpdateDto);

		userRepository.save(user);
	}

	private UserUpdateDto applyPatchToUser(JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, UserUpdateDto.class);
	}

	private void validatePasswordIfPresent(User user, UserUpdateDto dto) {
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

	private void hashPasswordIfPresent(User user, UserUpdateDto dto) {
		if (dto.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}
	}

	@Override
	@Transactional
	public void updateUserSimple(int userId, UserUpdateDto updateDto) {
		User user = find(userId);

		validatePasswordIfPresent(user, updateDto);
		validationService.validate(updateDto);
		userMapper.update(user, updateDto);
		hashPasswordIfPresent(user, updateDto);

		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findUserCredentialsByEmail(username).map(UserDetailsBuilder::buildUserDetails)
			.orElseThrow(() -> RecordNotFoundException.of(User.class, username));
	}

	private Optional<UserCredentialsDto> findUserCredentialsByEmail(String email) {
		return userRepository.findUserWithRolesByEmail(email).map(userMapper::toDetails);
	}

	@Override
	public UserResponseDto getUser(int userId) {
		User user = userRepository.findUserWithRolesById(userId)
			.orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
		UserResponseDto response = userMapper.toResponse(user);
		userPopulationService.populate(response);

		return calculateCalories(user, response);
	}

	@Override
	public AuthorDto getPublicUser(int userId) {
		User user = find(userId);
		AuthorDto authorDto = userMapper.toAuthorDto(user);
		userPopulationService.populate(authorDto);

		return authorDto;
	}

	@Override
	public int getUserIdByEmail(String email) {
		return userRepository.findByEmail(email)
			.map(User::getId)
			.orElseThrow(() -> RecordNotFoundException.of(User.class, email));
	}

}
