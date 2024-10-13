package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.PlanRepository;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.service.implementation.Workout.WorkoutPlanServiceImpl;

@ExtendWith(MockitoExtension.class)
public class WorkoutPlanServiceTest {
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
  void addWorkoutToPlan_shouldAdd_whenNotAlreadySavedAndPlanAndWorkoutFound() {

  }

  @Test
  void addWorkoutToPlan_shouldThrowException_whenWorkoutPlanAlreadyAdded() {

  }

  @Test
  void addWorkoutToPlan_shouldThrowException_whenPlanNotFound() {

  }

  @Test
  void addWorkoutToPlan_shouldThrowWorkout_whenWorkoutNotFound() {

  }

  @Test
  void deleteWorkoutFromPlan_shouldDelete_whenWorkoutPlanFound() {

  }

  @Test
  void deleteWorkoutFromPlan_shouldThrowException_whenWorkoutPlanNotFound() {

  }
}
