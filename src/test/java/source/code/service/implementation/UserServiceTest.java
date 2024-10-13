package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.UserMapper;
import source.code.repository.UserRepository;
import source.code.service.implementation.User.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private ValidationHelper validationHelper;
  @Mock
  private JsonPatchHelper jsonPatchHelper;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserServiceImpl userService;
  @BeforeEach
  void setup() {

  }

  @Test
  void register_shouldRegister() {

  }

  @Test
  void deleteUser_shouldDelete_whenUserFound() {

  }

  @Test
  void deleteUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void updateUser_shouldUpdate_whenPatched() {

  }

  @Test
  void updateUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void updateUser_shouldThrowException_whenPasswordsMatch() {

  }

  @Test
  void updateUser_shouldThrowException_whenValidationFails() {

  }

  @Test
  void loadUserByUsername_shouldLoad_whenUserFound() {

  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void getUser_shouldReturnUser_whenUserFound() {

  }

  @Test
  void getUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void getUserIdByEmail_shouldReturnUserId_whenUserFound() {

  }

  @Test
  void getUserIdByEmail_shouldThrowException_whenUserNotFound() {

  }

}
