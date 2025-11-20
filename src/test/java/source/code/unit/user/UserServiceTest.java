package source.code.unit.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import source.code.dto.pojo.UserCredentialsDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.request.user.UserUpdateDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.UserMapper;
import source.code.model.user.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.user.UserServiceImpl;

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
    @InjectMocks
    private UserServiceImpl userService;

    private int userId;
    private String email;
    private String password;
    private String newPassword;
    private User user;
    private UserCreateDto createDto;
    private UserResponseDto responseDto;
    private UserUpdateDto updateDto;
    private UserCredentialsDto userCredentialsDto;
    private JsonMergePatch patch;

    @BeforeEach
    void setup() {
        userId = 1;
        email = "test@example.com";
        password = "password";
        newPassword = "newPassword";
        user = new User();
        createDto = new UserCreateDto();
        responseDto = new UserResponseDto();
        updateDto = new UserUpdateDto();
        userCredentialsDto = new UserCredentialsDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    @DisplayName("register - Should create and return a new user")
    void register_ShouldCreateAndReturnNewUser() {
        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        userService.register(createDto);

        verify(userRepository).save(user);
        verify(userMapper).toEntity(createDto);
    }

    @Test
    @DisplayName("deleteUser - Should delete a user by ID")
    void deleteUser_ShouldDeleteUserById() {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);

        userService.deleteUser(userId);

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - Should throw exception when user not found")
    void deleteUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
        when(repositoryHelper.find(userRepository, User.class, userId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> userService.deleteUser(userId));

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("updateUser - Should update common")
    void updateUser_ShouldUpdate() throws Exception {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
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
    @DisplayName("updateUser - Should update password")
    void updateUser_ShouldUpdatePassword() throws Exception {
        String currentEncodedPassword = "encodedCurrentPassword";
        user.setPassword(currentEncodedPassword);
        updateDto.setOldPassword("currentPassword");
        updateDto.setPassword("newPassword");

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(userMapper).updateUserFromDto(user, updateDto);
        when(userRepository.save(user)).thenReturn(user);

        when(passwordEncoder.matches(eq("currentPassword"), eq(currentEncodedPassword)))
                .thenReturn(true);

        userService.updateUser(userId, patch);

        verify(repositoryHelper, times(1)).find(userRepository, User.class, userId);
        verify(validationService).validate(updateDto);
        verify(userMapper).updateUserFromDto(user, updateDto);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateUser - Should throw exception when user not found")
    void updateUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() throws Exception {
        when(repositoryHelper.find(userRepository, User.class, userId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class,
                () -> userService.updateUser(userId, patch));

        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("updateUser - Should throw exception when patch fails")
    void updateUser_ShouldThrowExceptionWhenPatchFails() throws Exception {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> userService.updateUser(userId, patch));

        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("updateUser - Should throw exception when updating password and old password is null")
    void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordIsNull() throws Exception {
        updateDto.setPassword("newPassword");
        updateDto.setOldPassword(null);

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, patch));

        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("updateUser - Should not updatePassword if old password is present and new is absent")
    void updateUser_ShouldNotUpdatePasswordIfOldPasswordIsPresentAndNewIsAbsent() throws Exception {
        String currentEncodedPassword = "encodedCurrentPassword";
        user.setPassword(currentEncodedPassword);
        updateDto.setOldPassword("currentPassword");

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
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
    @DisplayName("updateUser - Should throw exception when updating password and old password does not match")
    void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordDoesNotMatch() throws Exception {
        user.setPassword("encodedPassword");
        updateDto.setOldPassword("wrongPassword");
        updateDto.setPassword("newPassword");

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, patch));

        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("updateUser - Should throw exception when validation fails")
    void updateUser_ShouldThrowExceptionWhenValidationFails() throws Exception {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.createFromPatch(eq(patch), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class)
                .when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, patch));

        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("loadUserByUsername - Should return user details by username")
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
    @DisplayName("loadUserByUsername - Should throw exception when user not found")
    void loadUserByUsername_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
        String username = "nonexistent@example.com";

        when(userRepository.findUserWithRolesByEmail(username)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findUserWithRolesByEmail(username);
        verify(userMapper, never()).toDetails(any());
    }

    @Test
    @DisplayName("getUser - Should return user response by ID")
    void getUser_ShouldReturnUserResponseById() {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        userService.getUser(userId);

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("getUser - Should throw exception when user not found")
    void getUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
        when(repositoryHelper.find(userRepository, User.class, userId)).thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> userService.getUser(userId));

        verify(repositoryHelper).find(userRepository, User.class, userId);
    }

    @Test
    @DisplayName("getUserIdByEmail - Should return user ID by email")
    void getUserIdByEmail_ShouldReturnUserIdByEmail() {
        user.setId(userId);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.getUserIdByEmail(email);

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("getUserIdByEmail - Should throw exception when user not found by email")
    void getUserIdByEmail_ShouldThrowRecordNotFoundExceptionWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.getUserIdByEmail(email));

        verify(userRepository).findByEmail(email);
    }
}
