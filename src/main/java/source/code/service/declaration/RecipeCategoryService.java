package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;

import java.util.List;

public interface RecipeCategoryService {
  CategoryResponseDto createRecipeCategory(CategoryCreateDto request);
  void updateRecipeCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteRecipeCategory(int categoryId);
  List<CategoryResponseDto> getAllRecipeCategories();
  CategoryResponseDto getRecipeCategory(int categoryId);
}
