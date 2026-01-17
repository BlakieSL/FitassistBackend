package com.fitassist.backend.mapper.recipe;

import com.fitassist.backend.dto.request.recipe.RecipeFoodUpdateDto;
import com.fitassist.backend.model.recipe.RecipeFood;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class RecipeFoodMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "recipe", ignore = true)
	@Mapping(target = "food", ignore = true)
	public abstract void update(@MappingTarget RecipeFood recipeFood, RecipeFoodUpdateDto updateDto);

}
