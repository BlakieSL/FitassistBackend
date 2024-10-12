package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserPlanServiceTest {
  @Mock
  private UserPlanRepository userPlanRepository;
  @Mock
  private PlanRepository planRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserPlanServiceImpl userPlanService;
  @BeforeEach
  void setup() {

  }

  @Test
  void savePlanToUser_shouldSave_whenNotAlreadySavedAndPlanAnsUserFound() {

  }

  @Test
  void savePlanToUser_shouldThrowException_whenIsAlreadySaved() {

  }

  @Test
  void savePlanToUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void savePlanToUser_shouldThrowException_whenPlanNotFound() {

  }

  @Test
  void deleteSavedPlanFromUser_shouldDelete_whenUserPlanFound() {

  }

  @Test
  void deleteSavedPlanFromUser_shouldThrowException_whenUserPlanNotFound() {

  }

  @Test
  void calculatePlanLikesAndSaves_shouldCalculate_whenPlanFound() {

  }

  @Test
  void calculatePlanLikesAndSaves_shouldThrowException_whenPlanNotFound() {

  }

  @Test
  void calculatePlanLikesAndSaves_shouldReturnZeros_whenNoLikesAndSaves() {

  }
}
