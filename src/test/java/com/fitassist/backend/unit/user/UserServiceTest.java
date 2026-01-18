package com.fitassist.backend.unit.user;

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
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.helpers.JsonPatchServiceImpl;
import com.fitassist.backend.service.implementation.user.UserServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private ValidationService validationService;

	@Mock
	private JsonPatchServiceImpl jsonPatchService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private CalculationsService calculationsService;

	@InjectMocks
	private UserServiceImpl userService;

	private int userId;

	private String email;

	private User user;

	private UserCreateDto createDto;

	private UserResponseDto responseDto;

	private UserUpdateDto updateDto;

	private JsonMergePatch patch;

	@BeforeEach
	void setup() {
		userId = 1;
		email = "test@example.com";
		user = new User();
		createDto = new UserCreateDto();
		responseDto = new UserResponseDto();
		updateDto = new UserUpdateDto();
		patch = mock(JsonMergePatch.class);
	}

	@Test
	void register_ShouldCreateAndReturnNewUser() {
		String rawPassword = "password123";
		String encodedPassword = "encodedPassword";
		Role userRole = new Role();
		createDto.setPassword(rawPassword);

		when(userMapper.toEntity(createDto)).thenReturn(user);
		when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
		when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(userRole));
		when(userRepository.save(user)).thenReturn(user);
		when(userMapper.toResponse(user)).thenReturn(responseDto);

		userService.register(createDto);

		verify(userMapper).toEntity(createDto);
		verify(passwordEncoder).encode(rawPassword);
		verify(roleRepository).findByName(RoleEnum.USER);
		verify(userRepository).save(user);
		verify(userMapper).toResponse(user);
	}

	@Test
	void deleteUser_ShouldDeleteUserById() {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);

		userService.deleteUser(userId);

		verify(repositoryHelper).find(userRepository, User.class, userId);
		verify(userRepository).delete(user);
	}

	@Test
	void deleteUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenThrow(RecordNotFoundException.class);

		assertThrows(RecordNotFoundException.class, () -> userService.deleteUser(userId));

		verify(repositoryHelper).find(userRepository, User.class, userId);
		verify(userRepository, never()).delete(any());
	}

	@Test
	void updateUser_ShouldUpdate() throws Exception {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);
		doNothing().when(validationService).validate(updateDto);
		doNothing().when(userMapper).updateUserFromDto(user, updateDto);
		when(userRepository.save(user)).thenReturn(user);

		userService.updateUser(userId, patch);

		verify(repositoryHelper, times(1)).find(userRepository, User.class, userId);
		verify(validationService).validate(updateDto);
		verify(userMapper).updateUserFromDto(user, updateDto);
		verify(userRepository).save(user);
		verify(passwordEncoder, never()).matches(anyString(), anyString());
	}

	@Test
	void updateUser_ShouldUpdatePassword() throws Exception {
		String currentEncodedPassword = "encodedCurrentPassword";
		user.setPassword(currentEncodedPassword);
		updateDto.setOldPassword("currentPassword");
		updateDto.setPassword("newPassword");

		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);
		doNothing().when(validationService).validate(updateDto);
		doNothing().when(userMapper).updateUserFromDto(user, updateDto);
		when(userRepository.save(user)).thenReturn(user);

		when(passwordEncoder.matches(eq("currentPassword"), eq(currentEncodedPassword))).thenReturn(true);

		userService.updateUser(userId, patch);

		verify(repositoryHelper, times(1)).find(userRepository, User.class, userId);
		verify(validationService).validate(updateDto);
		verify(userMapper).updateUserFromDto(user, updateDto);
		verify(userRepository).save(user);
	}

	@Test
	void updateUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() throws Exception {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenThrow(RecordNotFoundException.class);

		assertThrows(RecordNotFoundException.class, () -> userService.updateUser(userId, patch));

		verify(userRepository, never()).save(user);
	}

	@Test
	void updateUser_ShouldThrowExceptionWhenPatchFails() throws Exception {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> userService.updateUser(userId, patch));

		verify(userRepository, never()).save(user);
	}

	@Test
	void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordIsNull() throws Exception {
		updateDto.setPassword("newPassword");
		updateDto.setOldPassword(null);

		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);

		assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, patch));

		verify(userRepository, never()).save(user);
	}

	@Test
	void updateUser_ShouldNotUpdatePasswordIfOldPasswordIsPresentAndNewIsAbsent() throws Exception {
		String currentEncodedPassword = "encodedCurrentPassword";
		user.setPassword(currentEncodedPassword);
		updateDto.setOldPassword("currentPassword");

		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);
		doNothing().when(validationService).validate(updateDto);
		doNothing().when(userMapper).updateUserFromDto(user, updateDto);
		when(userRepository.save(user)).thenReturn(user);

		userService.updateUser(userId, patch);

		verify(repositoryHelper, times(1)).find(userRepository, User.class, userId);
		verify(validationService).validate(updateDto);
		verify(userMapper).updateUserFromDto(user, updateDto);
		verify(userRepository).save(user);
		verify(passwordEncoder, never()).matches(any(), any());
	}

	@Test
	void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordDoesNotMatch() throws Exception {
		user.setPassword("encodedPassword");
		updateDto.setOldPassword("wrongPassword");
		updateDto.setPassword("newPassword");

		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);
		when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

		assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, patch));

		verify(userRepository, never()).save(user);
	}

	@Test
	void updateUser_ShouldThrowExceptionWhenValidationFails() throws Exception {
		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class))).thenReturn(updateDto);
		doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

		assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, patch));

		verify(userRepository, never()).save(user);
	}

	@Test
	void loadUserByUsername_ShouldReturnUserDetailsByUsername() {
		String username = "test@example.com";
		User user = new User();
		UserCredentialsDto credentialsDto = new UserCredentialsDto();
		credentialsDto.setEmail(username);
		credentialsDto.setPassword("encodedPassword");
		credentialsDto.setRoles(Set.of("user"));

		when(userRepository.findUserWithRolesByEmail(username)).thenReturn(Optional.of(user));
		when(userMapper.toDetails(user)).thenReturn(credentialsDto);

		UserDetails result = userService.loadUserByUsername(username);

		assertNotNull(result);
		assertEquals(username, result.getUsername());
		verify(userRepository).findUserWithRolesByEmail(username);
		verify(userMapper).toDetails(user);
	}

	@Test
	void loadUserByUsername_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
		String username = "nonexistent@example.com";

		when(userRepository.findUserWithRolesByEmail(username)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userService.loadUserByUsername(username));
		verify(userRepository).findUserWithRolesByEmail(username);
		verify(userMapper, never()).toDetails(any());
	}

	@Test
	void getUser_ShouldReturnUserResponseById() {
		when(userRepository.findUserWithRolesById(userId)).thenReturn(Optional.of(user));
		when(userMapper.toResponse(user)).thenReturn(responseDto);

		userService.getUser(userId);

		verify(userRepository).findUserWithRolesById(userId);
		verify(userMapper).toResponse(user);
	}

	@Test
	void getUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
		when(userRepository.findUserWithRolesById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userService.getUser(userId));

		verify(userRepository).findUserWithRolesById(userId);
	}

	@Test
	void getUserIdByEmail_ShouldReturnUserIdByEmail() {
		user.setId(userId);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		userService.getUserIdByEmail(email);

		verify(userRepository).findByEmail(email);
	}

	@Test
	void getUserIdByEmail_ShouldThrowRecordNotFoundExceptionWhenUserNotFoundByEmail() {
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userService.getUserIdByEmail(email));

		verify(userRepository).findByEmail(email);
	}

}
