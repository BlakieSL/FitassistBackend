package source.code.mapper.Recipe;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.other.RecipeCategoryShortDto;
import source.code.dto.request.Recipe.RecipeCreateDto;
import source.code.dto.request.Recipe.RecipeUpdateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategory;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.Helpers.RepositoryHelper;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {
  @Autowired
  private RecipeCategoryRepository recipeCategoryRepository;

  @Autowired
  private RepositoryHelper repositoryHelper;


  @Mapping(target = "categories", source = "recipeCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
  public abstract RecipeResponseDto toResponseDto(Recipe recipe);

  @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRecipes", ignore = true)
  @Mapping(target = "recipeFoods", ignore = true)
  public abstract Recipe toEntity(RecipeCreateDto dto);

  public abstract RecipeCategoryResponseDto toCategoryDto(RecipeCategory recipeCategory);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRecipes", ignore = true)
  @Mapping(target = "recipeFoods", ignore = true)
  public abstract void updateRecipe(@MappingTarget Recipe recipe, RecipeUpdateDto request);

  @Named("mapCategoryIdsToAssociations")
  protected Set<RecipeCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<RecipeCategoryAssociation> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      RecipeCategory category = repositoryHelper
              .find(recipeCategoryRepository, RecipeCategory.class, categoryId);

      RecipeCategoryAssociation association = RecipeCategoryAssociation
              .createWithRecipeCategory(category);

      associations.add(association);
    }

    return associations;
  }

  @Named("mapAssociationsToCategoryShortDto")
  protected List<RecipeCategoryShortDto> mapAssociationsToCategoryShortDto(Set<RecipeCategoryAssociation> associations) {
    return associations.stream()
            .map(association -> new RecipeCategoryShortDto(
                    association.getRecipeCategory().getId(),
                    association.getRecipeCategory().getName()
            ))
            .toList();
  }
}
