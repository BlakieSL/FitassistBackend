package com.fitassist.backend.mapper.recipe;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.pojo.RecipeFoodDto;
import com.fitassist.backend.dto.request.recipe.RecipeCreateDto;
import com.fitassist.backend.dto.request.recipe.RecipeUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.food.IngredientResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.dto.response.text.TextResponseDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.recipe.RecipeCategoryAssociation;
import com.fitassist.backend.model.recipe.RecipeFood;
import com.fitassist.backend.model.text.RecipeInstruction;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class RecipeMapper {

	private CommonMappingHelper commonMappingHelper;

	@Autowired
	private void setCommonMappingHelper(CommonMappingHelper commonMappingHelper) {
		this.commonMappingHelper = commonMappingHelper;
	}

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
	@Mapping(target = "ingredientsCount", ignore = true)
	@Mapping(target = "categories", source = "recipeCategoryAssociations",
			qualifiedByName = "mapAssociationsToCategoryResponseDto")
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract RecipeSummaryDto toSummaryDto(Recipe recipe);

	@Mapping(target = "recipeCategoryAssociations", ignore = true)
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userRecipes", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "recipeInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Recipe toEntity(RecipeCreateDto dto, @Context RecipeMappingContext context);

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
	public abstract void updateRecipe(@MappingTarget Recipe recipe, RecipeUpdateDto request,
			@Context RecipeMappingContext context);

	@AfterMapping
	protected void setRecipeAssociations(@MappingTarget Recipe recipe, RecipeCreateDto dto,
			@Context RecipeMappingContext context) {
		if (dto.getInstructions() != null) {
			List<RecipeInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instructionDto -> RecipeInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
						instructionDto.getText(), recipe))
				.toList();

			recipe.getRecipeInstructions().addAll(instructions);
		}

		if (context.getCategories() != null) {
			List<RecipeCategoryAssociation> associations = context.getCategories()
				.stream()
				.map(category -> RecipeCategoryAssociation.createWithRecipeAndCategory(recipe, category))
				.toList();

			recipe.getRecipeCategoryAssociations().addAll(associations);
		}
	}

	@AfterMapping
	protected void updateAssociations(@MappingTarget Recipe recipe, RecipeUpdateDto dto,
			@Context RecipeMappingContext context) {
		if (context.getCategories() != null) {
			recipe.getRecipeCategoryAssociations().clear();

			List<RecipeCategoryAssociation> associations = context.getCategories()
				.stream()
				.map(category -> RecipeCategoryAssociation.createWithRecipeAndCategory(recipe, category))
				.toList();

			recipe.getRecipeCategoryAssociations().addAll(associations);
		}

		if (dto.getInstructions() != null) {
			commonMappingHelper.updateTextAssociations(recipe.getRecipeInstructions(), dto.getInstructions(),
					instructionDto -> RecipeInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
							instructionDto.getText(), recipe));
		}
	}

	@AfterMapping
	protected void calculateTotalCalories(@MappingTarget RecipeResponseDto dto) {
		List<RecipeFoodDto> foods = Objects.requireNonNullElse(dto.getFoods(), List.of());

		BigDecimal totalCalories = foods.stream()
			.map(food -> food.getQuantity()
				.multiply(food.getIngredient().getFoodMacros().getCalories())
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
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
