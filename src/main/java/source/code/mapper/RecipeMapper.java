package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.other.RecipeCategoryShortDto;
import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.model.Recipe;
import source.code.model.RecipeCategory;
import source.code.model.RecipeCategoryAssociation;
import source.code.repository.RecipeCategoryRepository;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {
  @Autowired
  private RecipeCategoryRepository recipeCategoryRepository;

  @Mapping(target = "categories", source = "recipeCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
  public abstract RecipeResponseDto toDto(Recipe recipe);

  @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRecipes", ignore = true)
  @Mapping(target = "recipeFoods", ignore = true)
  public abstract Recipe toEntity(RecipeCreateDto dto);

  public abstract RecipeCategoryResponseDto toCategoryDto(RecipeCategory recipeCategory);

  @Named("mapCategoryIdsToAssociations")
  protected Set<RecipeCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<RecipeCategoryAssociation> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      RecipeCategory category = recipeCategoryRepository.findById(categoryId)
              .orElseThrow(() -> new NoSuchElementException(
                      "Category not found for id: " + categoryId));

      RecipeCategoryAssociation association =
              RecipeCategoryAssociation.createWithRecipeCategory(category);
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
