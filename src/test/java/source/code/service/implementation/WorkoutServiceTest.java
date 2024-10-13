package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.mapper.WorkoutMapper;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.repository.WorkoutTypeRepository;
import source.code.service.implementation.Workout.WorkoutServiceImpl;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {
  @Mock
  private WorkoutMapper workoutMapper;
  @Mock
  private WorkoutRepository workoutRepository;
  @Mock
  private WorkoutPlanRepository workoutPlanRepository;
  @Mock
  private WorkoutTypeRepository workoutTypeRepository;
  @InjectMocks
  private WorkoutServiceImpl workoutService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createWorkout_shouldCreate_whenWorkoutTypeFound() {

  }

  @Test
  void createWorkout_shouldThrowException_whenWorkoutTypeNotFound() {

  }

  @Test
  void getWorkout_shouldReturnWorkout_whenWorkoutFound() {

  }

  @Test
  void getWorkout_shouldThrowException_whenWorkoutNotFound() {

  }

  @Test
  void getAllWorkouts_shouldReturnWorkouts_whenWorkoutsFound() {

  }

  @Test
  void getAllWorkouts_shouldReturnEmptyList_whenNoWorkoutsFound() {

  }

  @Test
  void getWorkoutsByPlan_shouldReturnWorkoutPlan_whenWorkoutPlansFound() {

  }

  @Test
  void getWorkoutsByPlan_shouldReturnEmptyList_whenNoWorkoutPlansFound() {

  }
}
