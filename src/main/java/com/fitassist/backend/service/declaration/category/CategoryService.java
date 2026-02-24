package com.fitassist.backend.service.declaration.category;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import jakarta.json.JsonMergePatch;

import java.util.List;

public interface CategoryService {

	CategoryResponseDto createCategory(CategoryCreateDto request);

	void updateCategory(int categoryId, JsonMergePatch patch) throws JacksonException;

	void deleteCategory(int categoryId);

	List<CategoryResponseDto> getAllCategories();

	CategoryResponseDto getCategory(int categoryId);

}
