package source.code.mapper.Recipe;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.POJO.RecipeCategoryShortDto;
import source.code.dto.Request.Recipe.RecipeCreateDto;
import source.code.dto.Request.Recipe.RecipeUpdateDto;
import source.code.dto.Response.RecipeResponseDto;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategory;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.model.Text.RecipeInstruction;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.Declaration.Helpers.RepositoryHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
  @Mapping(target = "recipeInstructions", ignore = true)
  public abstract Recipe toEntity(RecipeCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRecipes", ignore = true)
  @Mapping(target = "recipeFoods", ignore = true)
  @Mapping(target = "recipeInstructions", ignore = true)
  public abstract void updateRecipe(@MappingTarget Recipe recipe, RecipeUpdateDto request);

  @AfterMapping
  protected void setRecipeAssociations(@MappingTarget Recipe recipe, RecipeCreateDto dto) {
    Set<RecipeInstruction> instructions = dto.getInstructions().stream()
            .map(instructionDto -> {
              RecipeInstruction instruction = RecipeInstruction
                      .createWithNumberTitleText(instructionDto.getNumber(),
                              instructionDto.getText(), instructionDto.getText());

              instruction.setRecipe(recipe);
              return instruction;
            }).collect(Collectors.toSet());

    recipe.getRecipeInstructions().addAll(instructions);
  }

  @Named("mapCategoryIdsToAssociations")
  protected Set<RecipeCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    return Optional.ofNullable(categoryIds)
            .orElseGet(List::of)
            .stream()
            .map(categoryId -> {
              RecipeCategory category = repositoryHelper
                      .find(recipeCategoryRepository, RecipeCategory.class, categoryId);
              return RecipeCategoryAssociation.createWithRecipeCategory(category);
            })
            .collect(Collectors.toSet());
  }

  @Named("mapAssociationsToCategoryShortDto")
  protected List<RecipeCategoryShortDto> mapAssociationsToCategoryShortDto(
          Set<RecipeCategoryAssociation> associations) {
    return associations.stream()
            .map(association -> new RecipeCategoryShortDto(
                    association.getRecipeCategory().getId(),
                    association.getRecipeCategory().getName()
            ))
            .toList();
  }
}
