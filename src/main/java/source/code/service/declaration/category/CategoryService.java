package source.code.service.declaration.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.response.category.CategoryResponseDto;

public interface CategoryService {

	CategoryResponseDto createCategory(CategoryCreateDto request);

	void updateCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteCategory(int categoryId);

	List<CategoryResponseDto> getAllCategories();

	CategoryResponseDto getCategory(int categoryId);

}
