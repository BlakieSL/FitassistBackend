package source.code.mapper.recipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.pojo.RecipeFoodDto;
import source.code.dto.response.food.IngredientResponseDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.dto.request.text.TextUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.dto.response.text.TextResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.model.TextType;
import source.code.mapper.helper.CommonMappingHelper;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategory;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.model.recipe.RecipeFood;
import source.code.model.text.RecipeInstruction;
import source.code.model.user.User;
import source.code.repository.RecipeCategoryRepository;
import source.code.repository.UserRepository;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class RecipeMapper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RecipeCategoryRepository recipeCategoryRepository;

	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "totalCalories", ignore = true)
	@Mapping(target = "categories", source = "recipeCategoryAssociations",
			qualifiedByName = "mapAssociationsToCategoryResponseDto")
	@Mapping(target = "instructions", source = "recipeInstructions", qualifiedByName = "mapInstructionsToDto")
	@Mapping(target = "imageUrls", ignore = true)
	@Mapping(target = "foods", source = "recipeFoods", qualifiedByName = "mapFoodsToDto")
	public abstract RecipeResponseDto toResponseDto(Recipe recipe);

	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "firstImageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "public", source = "isPublic")
	@Mapping(target = "ingredientsCount", ignore = true)
	@Mapping(target = "categories", source = "recipeCategoryAssociations",
			qualifiedByName = "mapAssociationsToCategoryResponseDto")
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract RecipeSummaryDto toSummaryDto(Recipe recipe);

	@Mapping(target = "recipeCategoryAssociations", ignore = true)
	@Mapping(target = "user", expression = "java(userIdToUser(userId))")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userRecipes", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "recipeInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Recipe toEntity(RecipeCreateDto dto, @Context int userId);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "recipeCategoryAssociations", ignore = true)
	@Mapping(target = "recipeInstructions", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "userRecipes", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void updateRecipe(@MappingTarget Recipe recipe, RecipeUpdateDto request);

	@AfterMapping
	protected void setRecipeAssociations(@MappingTarget Recipe recipe, RecipeCreateDto dto) {
		if (dto.getInstructions() != null) {
			List<RecipeInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instructionDto -> RecipeInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
						instructionDto.getText(), recipe))
				.toList();

			recipe.getRecipeInstructions().addAll(instructions);
		}

		if (dto.getCategoryIds() != null) {
			List<RecipeCategory> categories = recipeCategoryRepository.findAllByIdIn(dto.getCategoryIds());

			List<RecipeCategoryAssociation> associations = categories.stream()
				.map(category -> RecipeCategoryAssociation.createWithRecipeAndCategory(recipe, category))
				.toList();

			recipe.getRecipeCategoryAssociations().addAll(associations);
		}
	}

	@AfterMapping
	protected void updateAssociations(@MappingTarget Recipe recipe, RecipeUpdateDto dto) {
		if (dto.getCategoryIds() != null) {
			recipe.getRecipeCategoryAssociations().clear();

			List<RecipeCategory> categories = recipeCategoryRepository.findAllByIdIn(dto.getCategoryIds());

			List<RecipeCategoryAssociation> associations = categories.stream()
				.map(category -> RecipeCategoryAssociation.createWithRecipeAndCategory(recipe, category))
				.toList();

			recipe.getRecipeCategoryAssociations().addAll(associations);
		}

		if (dto.getInstructions() != null) {
			Set<RecipeInstruction> existingInstructions = recipe.getRecipeInstructions();

			List<Integer> updatedInstructionIds = dto.getInstructions()
				.stream()
				.map(TextUpdateDto::getId)
				.filter(Objects::nonNull)
				.toList();

			existingInstructions.removeIf(instruction -> !updatedInstructionIds.contains(instruction.getId()));

			for (TextUpdateDto instructionDto : dto.getInstructions()) {
				if (instructionDto.getId() != null) {
					existingInstructions.stream()
						.filter(instruction -> instruction.getId().equals(instructionDto.getId()))
						.findFirst()
						.ifPresent(instruction -> {
							if (instructionDto.getOrderIndex() != null) {
								instruction.setOrderIndex(instructionDto.getOrderIndex());
							}
							if (instructionDto.getText() != null) {
								instruction.setText(instructionDto.getText());
							}
							if (instructionDto.getTitle() != null) {
								instruction.setTitle(instructionDto.getTitle());
							}
						});
				}
				else {
					RecipeInstruction newInstruction = RecipeInstruction.of(instructionDto.getOrderIndex(),
							instructionDto.getTitle(), instructionDto.getText(), recipe);
					existingInstructions.add(newInstruction);
				}
			}
		}
	}

	@AfterMapping
	protected void calculateTotalCalories(@MappingTarget RecipeResponseDto dto) {
		List<RecipeFoodDto> foods = Objects.requireNonNullElse(dto.getFoods(), List.of());

		BigDecimal totalCalories = foods.stream()
			.map(food -> food.getQuantity().multiply(food.getIngredient().getFoodMacros().getCalories()))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		dto.setTotalCalories(totalCalories);
	}

	@Named("mapAssociationsToCategoryResponseDto")
	protected List<CategoryResponseDto> mapAssociationsToCategoryResponseDto(
			Set<RecipeCategoryAssociation> associations) {
		return associations.stream()
			.map(association -> new CategoryResponseDto(association.getRecipeCategory().getId(),
					association.getRecipeCategory().getName()))
			.toList();
	}

	@Named("userIdToUser")
	protected User userIdToUser(Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	@Named("mapInstructionsToDto")
	protected List<TextResponseDto> mapInstructionsToDto(Set<RecipeInstruction> instructions) {
		return instructions.stream()
			.map(instruction -> new TextResponseDto(instruction.getId(), instruction.getOrderIndex(),
					instruction.getText(), instruction.getTitle()))
			.toList();
	}

	@Named("mapFoodsToDto")
	protected List<RecipeFoodDto> mapFoodsToDto(Set<RecipeFood> foods) {
		return foods.stream().map(recipeFood -> {
			CategoryResponseDto categoryDto = new CategoryResponseDto(recipeFood.getFood().getFoodCategory().getId(),
					recipeFood.getFood().getFoodCategory().getName());

			FoodMacros macros = FoodMacros.of(recipeFood.getFood().getCalories(), recipeFood.getFood().getProtein(),
					recipeFood.getFood().getFat(), recipeFood.getFood().getCarbohydrates());

			IngredientResponseDto ingredient = new IngredientResponseDto(recipeFood.getFood().getId(),
					recipeFood.getFood().getName(), macros, categoryDto, null);

			return new RecipeFoodDto(recipeFood.getId(), recipeFood.getQuantity(), ingredient);
		}).toList();
	}

}
