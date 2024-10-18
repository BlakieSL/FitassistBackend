package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.dto.response.ExerciseCategoryResponseDto;

import java.util.List;

public interface ExerciseCategoryService {
  CategoryResponseDto createExerciseCategory(CategoryCreateDto request);

  void updateExerciseCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteExerciseCategory(int categoryId);
  List<CategoryResponseDto> getAllExerciseCategories();
  CategoryResponseDto getExerciseCategory(int categoryId);
}
