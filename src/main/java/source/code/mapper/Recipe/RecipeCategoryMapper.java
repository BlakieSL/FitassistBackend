package source.code.mapper.Recipe;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.mapper.Generics.BaseMapper;

import source.code.model.Recipe.RecipeCategory;

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
