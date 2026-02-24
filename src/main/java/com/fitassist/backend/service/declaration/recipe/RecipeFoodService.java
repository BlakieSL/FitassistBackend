package com.fitassist.backend.service.declaration.recipe;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.recipe.RecipeFoodCreateDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import jakarta.json.JsonMergePatch;

import java.util.List;

public interface RecipeFoodService {

	void saveFoodToRecipe(int recipeId, RecipeFoodCreateDto request);

	void replaceAllFoodsInRecipe(int recipeId, RecipeFoodCreateDto request);

	void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch) throws JacksonException;

	void deleteFoodFromRecipe(int foodId, int recipeId);

	List<FoodSummaryDto> getFoodsByRecipe(int recipeId);

}
