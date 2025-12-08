package source.code.service.declaration.recipe;

import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

import java.util.List;

public interface RecipePopulationService {
    void populate(List<RecipeSummaryDto> recipes);

    void populate(RecipeResponseDto recipe);
}
