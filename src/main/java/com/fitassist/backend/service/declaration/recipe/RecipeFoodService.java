package com.fitassist.backend.service.declaration.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.recipe.RecipeFoodCreateDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

public interface RecipeFoodService {

	void saveFoodToRecipe(int recipeId, RecipeFoodCreateDto request);

	void replaceAllFoodsInRecipe(int recipeId, RecipeFoodCreateDto request);

	void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException;

	void deleteFoodFromRecipe(int foodId, int recipeId);

	List<FoodSummaryDto> getFoodsByRecipe(int recipeId);

}
