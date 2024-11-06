package source.code.service.declaration.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.model.recipe.Recipe;

import java.util.List;

public interface RecipeService {
    RecipeResponseDto createRecipe(RecipeCreateDto dto);

    void updateRecipe(int recipeId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteRecipe(int recipeId);

    RecipeResponseDto getRecipe(int id);

    List<RecipeResponseDto> getAllRecipes();

    List<RecipeResponseDto> getFilteredRecipes(FilterDto filter);

    List<Recipe> getAllRecipeEntities();

    List<RecipeResponseDto> getRecipesByCategory(int categoryId);
}