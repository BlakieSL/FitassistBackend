package source.code.service.Declaration.Workout;

import source.code.dto.Request.Workout.WorkoutCreateDto;
import source.code.dto.Response.WorkoutResponseDto;

import java.util.List;

public interface WorkoutService {
  WorkoutResponseDto createWorkout(WorkoutCreateDto dto);

  WorkoutResponseDto getWorkout(int id);

  List<WorkoutResponseDto> getAllWorkouts();

  List<WorkoutResponseDto> getWorkoutsByPlan(int planId);
}