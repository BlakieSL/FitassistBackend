package source.code.service.declaration.Category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
  CategoryResponseDto createCategory(CategoryCreateDto request);
  void updateCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void  deleteCategory(int categoryId);

  List<CategoryResponseDto> getAllCategories();

  CategoryResponseDto getCategory(int categoryId);
}
