package source.code.service.declaration.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Recipe.RecipeFoodCreateDto;
import source.code.dto.response.FoodResponseDto;

import java.util.List;

public interface RecipeFoodService {
  void saveFoodToRecipe(int recipeId, int foodId, RecipeFoodCreateDto request);

  void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteFoodFromRecipe(int foodId, int recipeId);
  List<FoodResponseDto> getFoodsByRecipe(int recipeId);
}