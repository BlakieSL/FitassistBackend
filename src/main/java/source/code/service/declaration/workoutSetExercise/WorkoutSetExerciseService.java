package source.code.service.declaration.workoutSetExercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;

import java.util.List;

public interface WorkoutSetExerciseService {
    WorkoutSetExerciseResponseDto createWorkoutSetExercise(WorkoutSetExerciseCreateDto createDto);

    void updateWorkoutSetExercise(int workoutSetExerciseId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteWorkoutSetExercise(int workoutSetExerciseId);

    WorkoutSetExerciseResponseDto getWorkoutSetExercise(int workoutSetExerciseId);

    List<WorkoutSetExerciseResponseDto> getAllWorkoutSetExercisesForWorkoutSet(int workoutSetId);
}
