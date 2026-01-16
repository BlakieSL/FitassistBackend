package com.fitassist.backend.mapper.category;

import org.mapstruct.*;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.food.FoodCategory;

@Mapper(componentModel = "spring")
public abstract class FoodCategoryMapper implements BaseMapper<FoodCategory> {

	public abstract CategoryResponseDto toResponseDto(FoodCategory category);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "foods", ignore = true)
	public abstract FoodCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "foods", ignore = true)
	public abstract void updateEntityFromDto(@MappingTarget FoodCategory category, CategoryUpdateDto request);

}
