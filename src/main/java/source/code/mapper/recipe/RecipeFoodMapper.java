package source.code.mapper.recipe;

import org.mapstruct.*;
import source.code.dto.request.recipe.RecipeFoodUpdateDto;
import source.code.model.recipe.RecipeFood;

@Mapper(componentModel = "spring")
public abstract class RecipeFoodMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "recipe", ignore = true)
	@Mapping(target = "food", ignore = true)
	public abstract void update(@MappingTarget RecipeFood recipeFood, RecipeFoodUpdateDto updateDto);

}
