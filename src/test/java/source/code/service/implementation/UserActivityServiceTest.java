package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserActivityServiceTest {
  @Mock
  private UserActivityRepository userActivityRepository;
  @Mock
  private ActivityRepository activityRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserActivityServiceImpl userActivityService;
  @BeforeEach
  void setup() {

  }

  @Test
  void saveActivityToUser_shouldSave_whenNotSavedAndUserAndActivityFound() {

  }

  @Test
  void saveActivityToUser_shouldThrowException_whenAlreadySaved() {

  }

  @Test
  void saveActivityToUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void saveActivityToUser_shouldThrowException_whenActivityNotFound() {

  }

  @Test
  void deleteSavedActivityFromUser_shouldDelete_whenUserActivityFound() {

  }

  @Test
  void deleteSavedActivityFromUser_shouldThrowException_whenUserActivityNotFound() {

  }

  @Test
  void calculateActivityLikesAndSaves_shouldCalculate_whenActivityFound() {

  }

  @Test
  void calculateActivityLikesAndSaves_shouldThrowException_whenExerciseNotFound() {

  }

  @Test
  void calculateActivityLikesAndSaves_shouldReturnZero_whenUserActivityNotExist() {

  }
}
