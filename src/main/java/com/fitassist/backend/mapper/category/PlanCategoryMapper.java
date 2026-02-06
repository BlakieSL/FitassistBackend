package com.fitassist.backend.mapper.category;

import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.plan.PlanCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PlanCategoryMapper implements BaseMapper<PlanCategory> {

	public abstract CategoryResponseDto toResponse(PlanCategory category);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "planCategoryAssociations", ignore = true)
	public abstract PlanCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "planCategoryAssociations", ignore = true)
	public abstract void update(@MappingTarget PlanCategory category, CategoryUpdateDto request);

}
