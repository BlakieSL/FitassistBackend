package source.code.mapper.recipe;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.dto.pojo.RecipeFoodDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.dto.response.text.RecipeInstructionResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.media.Media;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategory;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.model.recipe.RecipeFood;
import source.code.model.text.RecipeInstruction;
import source.code.model.user.User;
import source.code.repository.RecipeCategoryRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorImageName", ignore = true)
    @Mapping(target = "authorImageUrl", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "dislikesCount", ignore = true)
    @Mapping(target = "savesCount", ignore = true)
    @Mapping(target = "liked", ignore = true)
    @Mapping(target = "disliked", ignore = true)
    @Mapping(target = "saved", ignore = true)
    @Mapping(target = "totalCalories", ignore = true)
    @Mapping(target = "categories", source = "recipeCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    @Mapping(target = "instructions", source = "recipeInstructions", qualifiedByName = "mapInstructionsToDto")
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "foods", source = "recipeFoods", qualifiedByName = "mapFoodsToDto")
    public abstract RecipeResponseDto toResponseDto(Recipe recipe);

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorImageName", ignore = true)
    @Mapping(target = "authorImageUrl", ignore = true)
    @Mapping(target = "firstImageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
    @Mapping(target = "firstImageUrl", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "dislikesCount", ignore = true)
    @Mapping(target = "savesCount", ignore = true)
    @Mapping(target = "ingredientsCount", ignore = true)
    @Mapping(target = "categories", source = "recipeCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    @Mapping(target = "userRecipeInteractionCreatedAt", ignore = true)
    public abstract RecipeSummaryDto toSummaryDto(Recipe recipe);

    @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "user", expression = "java(userIdToUser(userId))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRecipes", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "recipeInstructions", ignore = true)
    public abstract Recipe toEntity(RecipeCreateDto dto, @Context int userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userRecipes", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "recipeInstructions", ignore = true)
    public abstract void updateRecipe(@MappingTarget Recipe recipe, RecipeUpdateDto request);

    @AfterMapping
    protected void setRecipeAssociations(@MappingTarget Recipe recipe, RecipeCreateDto dto) {
        if (dto.getInstructions() == null) {
            return;
        }
        List<RecipeInstruction> instructions = dto.getInstructions().stream()
                .map(instructionDto -> RecipeInstruction.of(
                        instructionDto.getOrderIndex(),
                        instructionDto.getText(),
                        instructionDto.getText(),
                        recipe
                )).toList();

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

    @Named("userIdToUser")
    protected User userIdToUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }

    @Named("mapInstructionsToDto")
    protected List<RecipeInstructionResponseDto> mapInstructionsToDto(Set<RecipeInstruction> instructions) {
        return instructions.stream()
                .map(instruction -> new RecipeInstructionResponseDto(
                        instruction.getId(),
                        instruction.getOrderIndex(),
                        instruction.getTitle(),
                        instruction.getText()
                ))
                .toList();
    }

    @Named("mapFoodsToDto")
    protected List<RecipeFoodDto> mapFoodsToDto(Set<RecipeFood> foods) {
        return foods.stream()
                .map(recipeFood -> new RecipeFoodDto(
                        recipeFood.getId(),
                        recipeFood.getQuantity(),
                        recipeFood.getFood().getId(),
                        recipeFood.getFood().getName(),
                        recipeFood.getFood().getCalories()
                ))
                .toList();
    }

    @Named("mapMediaToFirstImageName")
    protected String mapMediaToFirstImageName(List<Media> mediaList) {
        if (mediaList.isEmpty()) return null;
        return mediaList.getFirst().getImageName();
    }

    @AfterMapping
    protected void calculateTotalCalories(@MappingTarget RecipeResponseDto dto) {
        List<RecipeFoodDto> foods = Objects.requireNonNullElse(dto.getFoods(), List.of());

        BigDecimal totalCalories = foods.stream()
                    .map(food -> food.getQuantity().multiply(food.getFoodCalories()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalCalories(totalCalories);
    }
}
