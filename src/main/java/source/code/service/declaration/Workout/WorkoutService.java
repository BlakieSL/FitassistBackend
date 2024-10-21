package source.code.service.declaration.Workout;

import source.code.dto.response.WorkoutResponseDto;
import source.code.dto.request.Workout.WorkoutCreateDto;

import java.util.List;

public interface WorkoutService {
  WorkoutResponseDto createWorkout(WorkoutCreateDto dto);

  WorkoutResponseDto getWorkout(int id);

  List<WorkoutResponseDto> getAllWorkouts();

  List<WorkoutResponseDto> getWorkoutsByPlan(int planId);
}