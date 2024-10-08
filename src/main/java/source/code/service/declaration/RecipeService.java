package source.code.service.declaration;

import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;

import java.util.List;

public interface RecipeService {
    RecipeResponseDto createRecipe(RecipeCreateDto dto);
    RecipeResponseDto getRecipe(int id);
    List<RecipeResponseDto> getAllRecipes();
    List<RecipeResponseDto> getRecipesByUser(int userId);
    List<RecipeCategoryResponseDto> getAllCategories();
    List<RecipeResponseDto> getRecipesByCategory(int categoryId);
}