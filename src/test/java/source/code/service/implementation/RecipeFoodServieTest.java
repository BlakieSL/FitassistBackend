package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import source.code.repository.*;

@ExtendWith(MockitoExtension.class)
public class RecipeFoodServieTest {
  @Mock
  private WorkoutPlanRepository workoutPlanRepository;
  @Mock
  private WorkoutRepository workoutRepository;
  @Mock
  private PlanRepository planRepository;
  @InjectMocks
  private WorkoutPlanServiceImpl workoutPlanService;
  @BeforeEach
  void setup() {

  }

  @Test
  void addFoodToRecipe_shouldAdd_whenNotAlreadyAdded() {

  }

  @Test
  void addFoodToRecipe_shouldThrowException_whenIsAlreadyAdded() {

  }

  @Test
  void addFoodToRecipe_shouldThrowException_whenRecipeNotFound() {

  }

  @Test
  void addFoodToRecipe_shouldThrowException_whenFoodNotFound() {

  }

  @Test
  void deleteFoodFromRecipe_shouldDelete_whenRecipeFoodFound() {

  }

  @Test
  void deleteFoodFromRecipe_shouldThrowException_whenRecipeFoodNotFound() {

  }

  @Test
  void updateFoodRecipe_shouldUpdate_whenRecipeFoodFoundValidationPassed() {

  }

  @Test
  void updateFoodRecipe_shouldThrowException_whenRecipeFoodNotFound() {

  }

  @Test
  void updateFoodRecipe_shouldThrowException_whenValidationFails() {

  }

  @Test
  void update_shouldNotSet_whenAmountsNotEqual() {

  }
}
