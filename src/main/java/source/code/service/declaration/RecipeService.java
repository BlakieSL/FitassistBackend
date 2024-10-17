package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;

import java.util.List;

public interface RecipeService {
  RecipeResponseDto createRecipe(RecipeCreateDto dto);

  void updateRecipe(int recipeId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteRecipe(int recipeId);

  RecipeResponseDto getRecipe(int id);

  List<RecipeResponseDto> getAllRecipes();

  List<RecipeCategoryResponseDto> getAllCategories();

  List<RecipeResponseDto> getRecipesByCategory(int categoryId);
}