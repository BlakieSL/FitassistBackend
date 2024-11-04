package source.code.service.Declaration.Workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Workout.WorkoutCreateDto;
import source.code.dto.Response.Workout.WorkoutResponseDto;

import java.util.List;

public interface WorkoutService {
    WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto);

    void updateWorkout(int workoutId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException;

    void deleteWorkout(int workoutId);

    WorkoutResponseDto getWorkout(int id);

    List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId);
}
