package unit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import source.code.dto.pojo.UserCredentialsDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.request.user.UserUpdateDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.UserDetailsHelper;
import source.code.mapper.UserMapper;
import source.code.model.user.profile.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.user.UserService;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.user.UserServiceImpl;

import java.util.Optional;

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

    private static final int USER_ID = 1;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";

    @Test
    @DisplayName("register - Should create and return a new user")
    void register_ShouldCreateAndReturnNewUser() {
        UserCreateDto request = new UserCreateDto();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        UserResponseDto response = new UserResponseDto();
        response.setId(USER_ID);
        response.setEmail(EMAIL);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponseDto result = userService.register(request);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());
        verify(userRepository).save(user);
        verify(userMapper).toEntity(request);
    }

    @Test
    @DisplayName("deleteUser - Should delete a user by ID")
    void deleteUser_ShouldDeleteUserById() {
        User user = new User();
        user.setId(USER_ID);

        when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);

        userService.deleteUser(USER_ID);

        verify(repositoryHelper).find(userRepository, User.class, USER_ID);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - Should throw exception when user not found")
    void deleteUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {
        when(repositoryHelper.find(userRepository, User.class, USER_ID))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> userService.deleteUser(USER_ID));

        verify(repositoryHelper).find(userRepository, User.class, USER_ID);
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("updateUser - Should update common")
    void updateUser_ShouldUpdate() throws Exception {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setEmail("old@example.com");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setEmail("old@example.com");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("new@example.com");

        JsonMergePatch patch = mock(JsonMergePatch.class);

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.applyPatch(eq(patch), any(UserResponseDto.class), eq(UserUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(userMapper).updateUserFromDto(user, updateDto);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(userId, patch);

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(jsonPatchService).applyPatch(patch, responseDto, UserUpdateDto.class);
        verify(validationService).validate(updateDto);
        verify(userMapper).updateUserFromDto(user, updateDto);
        verify(userRepository).save(user);
        verify(passwordEncoder, never()).matches(anyString(), anyString());

        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("updateUser - Should update password")
    void updateUser_ShouldUpdatePassword() throws Exception {
        // Arrange
        int userId = 1;
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(oldPassword)); // Stored encoded password

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setOldPassword(oldPassword);
        updateDto.setPassword(newPassword);

        JsonMergePatch patch = mock(JsonMergePatch.class);

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(jsonPatchService.applyPatch(any(), eq(responseDto), any())).thenReturn(updateDto);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        // Act
        userService.updateUser(userId, patch);

        // Assert
        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(jsonPatchService).applyPatch(any(), eq(responseDto), any());
        verify(passwordEncoder).matches(oldPassword, user.getPassword());
        verify(validationService).validate(updateDto);
        verify(userMapper).updateUserFromDto(user, updateDto);
        verify(userRepository).save(user);

        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("updateUser - Should throw exception when user not found")
    void updateUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should throw exception when patch fails")
    void updateUser_ShouldThrowExceptionWhenPatchFails() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should throw exception when updating password and old password is null")
    void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordIsNull() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should throw exception when updating password and new password is null")
    void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndNewPasswordIsNull() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should not updatePassword if old password is present and new is absent")
    void updateUser_ShouldNotUpdatePasswordIfOldPasswordIsPresentAndNewIsAbsent() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should throw exception when updating password and old password does not match")
    void updateUser_ShouldThrowExceptionWhenUpdatingPasswordAndOldPasswordDoesNotMatch() throws Exception {

    }

    @Test
    @DisplayName("updateUser - Should throw exception when validation fails")
    void updateUser_ShouldThrowExceptionWhenValidationFails() throws Exception {

    }

    @Test
    @DisplayName("loadUserByUsername - Should return user details by username")
    void loadUserByUsername_ShouldReturnUserDetailsByUsername() {

    }

    @Test
    @DisplayName("loadUserByUsername - Should throw exception when user not found")
    void loadUserByUsername_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {

    }

    @Test
    @DisplayName("getUser - Should return user response by ID")
    void getUser_ShouldReturnUserResponseById() {

    }

    @Test
    @DisplayName("getUser - Should throw exception when user not found")
    void getUser_ShouldThrowRecordNotFoundExceptionWhenUserNotFound() {

    }

    @Test
    @DisplayName("getUserIdByEmail - Should return user ID by email")
    void getUserIdByEmail_ShouldReturnUserIdByEmail() {

    }

    @Test
    @DisplayName("getUserIdByEmail - Should throw exception when user not found by email")
    void getUserIdByEmail_ShouldThrowRecordNotFoundExceptionWhenUserNotFoundByEmail() {

    }
}