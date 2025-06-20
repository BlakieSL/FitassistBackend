package source.code.service.declaration.workoutSetGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupCreateDto;
import source.code.dto.response.workoutSetGroup.WorkoutSetGroupResponseDto;

import java.util.List;

public interface WorkoutSetGroupService {
    WorkoutSetGroupResponseDto createWorkoutSetGroup(WorkoutSetGroupCreateDto createDto);
    void updateWorkoutSetGroup(int workoutSetGroupId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
    void deleteWorkoutSetGroup(int workoutSetGroupId);
    WorkoutSetGroupResponseDto getWorkoutSetGroup(int workoutSetGroupId);
    List<WorkoutSetGroupResponseDto> getAllWorkoutSetGroupsForWorkout(int workoutId);
}
