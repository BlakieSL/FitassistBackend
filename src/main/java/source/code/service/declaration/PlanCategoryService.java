package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;

import java.util.List;

public interface PlanCategoryService {
  CategoryResponseDto createPlanCategory(CategoryCreateDto request);
  void updatePlanCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deletePlanCategory(int categoryId);
  List<CategoryResponseDto> getAllPlanCategories();
  CategoryResponseDto getPlanCategory(int categoryId);
}
