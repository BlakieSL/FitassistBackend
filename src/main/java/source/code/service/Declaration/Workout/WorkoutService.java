package source.code.service.Declaration.Workout;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Workout.WorkoutCreateDto;
import source.code.dto.Response.Workout.WorkoutResponseDto;
import source.code.model.Workout.Workout;

import java.util.List;

public interface WorkoutService {
  WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto);
  void updateWorkout(int workoutId, JsonMergePatch patch);
  void deleteWorkout(int workoutId);
  WorkoutResponseDto getWorkout(int id);
  List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId);
}
