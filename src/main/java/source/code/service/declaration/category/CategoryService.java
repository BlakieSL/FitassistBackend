package source.code.service.declaration.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.category.CategoryCreateDto;
import source.code.dto.Response.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto request);

    void updateCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteCategory(int categoryId);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategory(int categoryId);
}
