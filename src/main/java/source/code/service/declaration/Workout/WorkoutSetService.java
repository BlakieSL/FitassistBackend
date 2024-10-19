package source.code.service.declaration.Workout;

import source.code.dto.WorkoutSetDto;

import java.util.List;

public interface WorkoutSetService {
  WorkoutSetDto createWorkoutSet(WorkoutSetDto workoutSetDto);

  void deleteWorkoutSet(int id);

  WorkoutSetDto getWorkoutSet(int id);

  List<WorkoutSetDto> getAllWorkoutSets();

  List<WorkoutSetDto> getWorkoutSetsByWorkoutType(int workoutTypeId);
}