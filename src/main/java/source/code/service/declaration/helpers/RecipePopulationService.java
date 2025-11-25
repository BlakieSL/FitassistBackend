package source.code.service.declaration.helpers;

import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.model.user.TypeOfInteraction;

import java.util.List;

public interface RecipePopulationService {
    void populate(List<RecipeSummaryDto> recipes);

    void populate(RecipeResponseDto recipe);

    void populateInteractionDates(List<RecipeSummaryDto> recipes, int userId, TypeOfInteraction type);
}
