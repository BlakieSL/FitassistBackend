package source.code.service.Declaration.Workout;

import source.code.dto.Request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.Response.WorkoutSetResponseDto;

import java.util.List;

public interface WorkoutSetService {
  WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto workoutSetResponseDto);

  void deleteWorkoutSet(int id);

  WorkoutSetResponseDto getWorkoutSet(int id);

  List<WorkoutSetResponseDto> getAllWorkoutSets();

  List<WorkoutSetResponseDto> getWorkoutSetsByWorkoutType(int workoutTypeId);
}