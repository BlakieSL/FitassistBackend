package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserFoodServiceTest {
  @Mock
  private UserFoodRepository userFoodRepository;
  @Mock
  private FoodRepository foodRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserFoodServiceImpl userFoodService;
  @BeforeEach
  void setup() {

  }

  @Test
  void saveFoodToUser_shouldSave_whenNotAlreadySavedAndUserAndFoodFound() {

  }

  @Test
  void saveFoodToUser_shouldThrowException_whenFoodAlreadySaved() {

  }

  @Test
  void saveFoodToUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void saveFoodToUser_shouldThrowException_whenFoodNotFound() {

  }

  @Test
  void deleteSavedFoodFromUser_shouldDelete_whenUserFoodFound() {

  }

  @Test
  void deleteSavedFoodFromUser_shouldTrowException_whenUserFoodNotFound() {

  }

  @Test
  void calculateFoodLikesAndSaves_shouldCalculate_whenFoodFound() {

  }

  @Test
  void calculateFoodLikesAndSaves_shouldThrowException_whenFoodNotFound() {

  }
}
