package source.code.mapper.Recipe;

import org.mapstruct.*;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.mapper.Generics.BaseMapper;
import source.code.model.Recipe.RecipeCategory;

@Mapper(componentModel = "spring")
public abstract class RecipeCategoryMapper implements BaseMapper<RecipeCategory> {
  public abstract CategoryResponseDto toResponseDto(RecipeCategory category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "recipeCategoryAssociations", ignore = true)
  public abstract RecipeCategory toEntity(CategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "recipeCategoryAssociations", ignore = true)
  public abstract void updateEntityFromDto(@MappingTarget RecipeCategory category, CategoryUpdateDto request);
}
