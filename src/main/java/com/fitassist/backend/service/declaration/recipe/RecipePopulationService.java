package com.fitassist.backend.service.declaration.recipe;

import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;

import java.util.List;

public interface RecipePopulationService {

	void populate(List<RecipeSummaryDto> recipes);

	void populate(RecipeResponseDto recipe);

}
