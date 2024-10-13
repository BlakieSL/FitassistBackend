package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.User.UserExerciseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserExerciseServiceTest {
  @Mock
  private UserExerciseRepository userExerciseRepository;
  @Mock
  private ExerciseRepository exerciseRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserExerciseServiceImpl userExerciseService;
  @BeforeEach
  void setup() {

  }

  @Test
  void saveExerciseToUser_shouldSave_whenNotSavedAndUserAndExerciseFound() {

  }

  @Test
  void saveExerciseToUser_shouldThrowException_whenAlreadySaved() {

  }

  @Test
  void saveExerciseToUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void saveExerciseToUser_shouldThrowException_whenExerciseNotFound() {

  }

  @Test
  void deleteSavedExerciseFromUser_shouldDelete_whenUserExerciseFound() {

  }

  @Test
  void deleteSavedExerciseFromUser_shouldThrowException_whenUserExerciseNotFound() {

  }

  @Test
  void calculateExerciseLikesAndSaves_shouldCalculate_whenExerciseFound() {

  }

  @Test
  void calculateExerciseLikesAndSaves_shouldThrowException_whenExerciseNotFound() {

  }

  @Test
  void calculateExerciseLikesAndSaves_shouldReturnZeros_whenThereAreNoLikesAndSaves() {

  }
}
