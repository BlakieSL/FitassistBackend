package com.fitassist.backend.service.declaration.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

public interface CategoryService {

	CategoryResponseDto createCategory(CategoryCreateDto request);

	void updateCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteCategory(int categoryId);

	List<CategoryResponseDto> getAllCategories();

	CategoryResponseDto getCategory(int categoryId);

}
