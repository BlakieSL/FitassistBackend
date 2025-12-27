package source.code.service.declaration.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.response.workout.WorkoutResponseDto;

public interface WorkoutService {

	WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto);

	void updateWorkout(int workoutId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteWorkout(int workoutId);

	WorkoutResponseDto getWorkout(int id);

	List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId);

}
