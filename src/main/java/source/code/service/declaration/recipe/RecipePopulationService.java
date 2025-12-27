package source.code.service.declaration.recipe;

import java.util.List;

import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

public interface RecipePopulationService {

	void populate(List<RecipeSummaryDto> recipes);

	void populate(RecipeResponseDto recipe);

}
