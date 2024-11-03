package source.code.service.Declaration.WorkoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.Response.WorkoutSet.WorkoutSetResponseDto;

import java.util.List;

public interface WorkoutSetService {
  WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto createDto);
  void updateWorkoutSet(int workoutSetId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
  void deleteWorkoutSet(int workoutSetId);
  WorkoutSetResponseDto getWorkoutSet(int workoutSetId);
  List<WorkoutSetResponseDto> getAllWorkoutSetsForWorkout(int workoutId);
}
