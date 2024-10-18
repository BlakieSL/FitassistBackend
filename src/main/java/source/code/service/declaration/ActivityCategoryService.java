package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;

import java.util.List;

public interface ActivityCategoryService {
  CategoryResponseDto createActivityCategory(CategoryCreateDto request);

  void updateActivityCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteActivityCategory(int categoryId);
  List<CategoryResponseDto> getAllActivityCategories();

  CategoryResponseDto getActivityCategory(int categoryId);
}
