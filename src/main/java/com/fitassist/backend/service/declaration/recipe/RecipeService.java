package com.fitassist.backend.service.declaration.recipe;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.recipe.RecipeCreateDto;
import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.model.recipe.Recipe;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

	RecipeResponseDto createRecipe(RecipeCreateDto dto);

	void updateRecipe(int recipeId, JsonMergePatch patch) throws JacksonException;

	void deleteRecipe(int recipeId);

	RecipeResponseDto getRecipe(int id);

	Page<RecipeSummaryDto> getFilteredRecipes(FilterDto filter, Pageable pageable);

	List<Recipe> getAllRecipeEntities();

	Long incrementViews(int recipeId);

}
