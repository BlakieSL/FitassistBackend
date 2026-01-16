package com.fitassist.backend.mapper.category;

import org.mapstruct.*;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.plan.PlanCategory;

@Mapper(componentModel = "spring")
public abstract class PlanCategoryMapper implements BaseMapper<PlanCategory> {

	public abstract CategoryResponseDto toResponseDto(PlanCategory category);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "planCategoryAssociations", ignore = true)
	public abstract PlanCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "planCategoryAssociations", ignore = true)
	public abstract void updateEntityFromDto(@MappingTarget PlanCategory category, CategoryUpdateDto request);

}
