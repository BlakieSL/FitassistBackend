package source.code.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.RecipeFoodCreateDto;

public interface RecipeFoodService {
    void addFoodToRecipe(int recipeId, int foodId, RecipeFoodCreateDto request);
    void deleteFoodFromRecipe(int foodId, int recipeId);
    void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
}