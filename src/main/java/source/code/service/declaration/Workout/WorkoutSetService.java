package source.code.service.declaration.Workout;

import source.code.dto.request.Workout.WorkoutCreateDto;
import source.code.dto.request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.response.WorkoutSetResponseDto;

import java.util.List;

public interface WorkoutSetService {
  WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto workoutSetResponseDto);

  void deleteWorkoutSet(int id);

  WorkoutSetResponseDto getWorkoutSet(int id);

  List<WorkoutSetResponseDto> getAllWorkoutSets();

  List<WorkoutSetResponseDto> getWorkoutSetsByWorkoutType(int workoutTypeId);
}