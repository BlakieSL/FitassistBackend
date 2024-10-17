package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.ExerciseCategoryCreateDto;
import source.code.dto.response.ExerciseCategoryResponseDto;

import java.util.List;

public interface ExerciseCategoryService {
  List<ExerciseCategoryResponseDto> getAllCategories();
  ExerciseCategoryResponseDto getExerciseCategory(int exerciseCategoryId);

  ExerciseCategoryResponseDto createExerciseCategory(ExerciseCategoryCreateDto request);

  void updateExercise(int exerciseCategoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteExercise(int exerciseCategoryId);
}
