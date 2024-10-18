package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;

import java.util.List;

public interface FoodCategoryService {
  CategoryResponseDto createFoodCategory(CategoryCreateDto request);

  void updateFoodCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteFoodCategory(int categoryId);

  List<CategoryResponseDto> getAllFoodCategories();

  CategoryResponseDto getFoodCategory(int categoryId);
}
