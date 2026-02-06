package com.fitassist.backend.mapper.category;

import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.recipe.RecipeCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class RecipeCategoryMapper implements BaseMapper<RecipeCategory> {

	public abstract CategoryResponseDto toResponse(RecipeCategory category);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "recipeCategoryAssociations", ignore = true)
	public abstract RecipeCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "recipeCategoryAssociations", ignore = true)
	public abstract void update(@MappingTarget RecipeCategory category, CategoryUpdateDto request);

}
