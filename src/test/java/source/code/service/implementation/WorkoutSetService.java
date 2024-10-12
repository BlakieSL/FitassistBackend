package source.code.service.implementation;

import groovy.transform.TailRecursive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.mapper.WorkoutSetMapper;
import source.code.repository.ExerciseRepository;
import source.code.repository.WorkoutSetRepository;
import source.code.repository.WorkoutTypeRepository;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetService {
  @Mock
  private WorkoutSetMapper workoutSetMapper;
  @Mock
  private WorkoutSetRepository workoutSetRepository;
  @Mock
  private WorkoutTypeRepository workoutTypeRepository;
  @Mock
  private ExerciseRepository exerciseRepository;
  @InjectMocks
  private WorkoutSetServiceImpl workoutSetService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createWorkoutSet_shouldCreate_whenWorkoutTypeAndExerciseFound() {

  }

  @Test
  void createWorkoutSet_shouldThrowException_whenWorkoutTypeNotFound() {

  }

  @Test
  void createWorkoutSet_shouldThrowException_whenExerciseNotFound() {

  }

  @Test
  void deleteWorkoutSet_shouldDelete_whenWorkoutSetFound() {

  }

  @Test
  void deleteWorkoutSet_shouldThrowException_whenWorkoutSetNotFound() {

  }

  @Test
  void getWorkoutSet_shouldReturnWorkoutSet_whenWorkoutSetFound() {

  }

  @Test
  void getWorkoutSet_shouldThrowException_whenWorkoutSetNotFound() {

  }

  @Test
  void getAllWorkoutSets_shouldReturnWorkoutSets() {

  }

  @Test
  void getAllWorkoutSets_shouldReturnEmptyList_whenNoWorkoutSetsFound() {

  }

  @Test
  void getWorkoutSetsByWorkoutType_shouldReturnWorkoutSets() {

  }

  @Test
  void getWorkoutSetsByWorkoutType_shouldReturnEmptyList_whenNoWorkoutSetsFound() {

  }
}
