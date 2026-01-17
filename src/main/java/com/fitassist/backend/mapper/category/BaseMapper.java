package com.fitassist.backend.mapper.category;

import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import org.mapstruct.MappingTarget;

public interface BaseMapper<T> {

	CategoryResponseDto toResponseDto(T entity);

	T toEntity(CategoryCreateDto request);

	void updateEntityFromDto(@MappingTarget T entity, CategoryUpdateDto request);

}
