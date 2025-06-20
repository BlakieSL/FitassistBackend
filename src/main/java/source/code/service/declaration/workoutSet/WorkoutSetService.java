package source.code.service.declaration.workoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;

import java.util.List;

public interface WorkoutSetService {
    WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto createDto);

    void updateWorkoutSet(int workoutSetId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteWorkoutSet(int workoutSetId);

    WorkoutSetResponseDto getWorkoutSet(int workoutSetId);

    List<WorkoutSetResponseDto> getAllWorkoutSetsForWorkoutSetGroup(int workoutSetGroupId);
}
