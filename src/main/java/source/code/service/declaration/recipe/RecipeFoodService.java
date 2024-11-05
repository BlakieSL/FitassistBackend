package source.code.service.declaration.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Recipe.RecipeFoodCreateDto;
import source.code.dto.Response.FoodResponseDto;
import source.code.dto.Response.RecipeResponseDto;

import java.util.List;

public interface RecipeFoodService {
    void saveFoodToRecipe(int recipeId, int foodId, RecipeFoodCreateDto request);

    void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteFoodFromRecipe(int foodId, int recipeId);

    List<FoodResponseDto> getFoodsByRecipe(int recipeId);

    List<RecipeResponseDto> getRecipesByFood(int foodId);
}