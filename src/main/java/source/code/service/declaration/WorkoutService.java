package source.code.service.declaration;

import source.code.dto.WorkoutDto;

import java.util.List;

public interface WorkoutService {
  WorkoutDto createWorkout(WorkoutDto dto);

  WorkoutDto getWorkout(int id);

  List<WorkoutDto> getAllWorkouts();

  List<WorkoutDto> getWorkoutsByPlan(int planId);
}