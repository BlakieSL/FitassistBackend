package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.ActivityCategoryCreateDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivityResponseDto;

import java.util.List;

public interface ActivityCategoryService {
  List<ActivityCategoryResponseDto> getAllCategories();

  ActivityCategoryResponseDto getById(int categoryId);

  ActivityCategoryResponseDto createCategory(ActivityCategoryCreateDto request);

  void updateCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteCategory(int categoryId);
}
