package source.code.mapper.recipe;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import source.code.dto.Request.Recipe.RecipeFoodCreateDto;
import source.code.model.recipe.RecipeFood;

@Mapper(componentModel = "spring")
public abstract class RecipeFoodMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(@MappingTarget RecipeFood recipeFood, RecipeFoodCreateDto updateDto);
}
