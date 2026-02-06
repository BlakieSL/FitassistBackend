package com.fitassist.backend.mapper.category;

import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.thread.ThreadCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ThreadCategoryMapper implements BaseMapper<ThreadCategory> {

	public abstract CategoryResponseDto toResponse(ThreadCategory category);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "threads", ignore = true)
	public abstract ThreadCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "threads", ignore = true)
	public abstract void update(@MappingTarget ThreadCategory category, CategoryUpdateDto request);

}
