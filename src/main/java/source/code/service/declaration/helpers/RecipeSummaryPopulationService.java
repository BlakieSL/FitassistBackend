package source.code.service.declaration.helpers;

import source.code.dto.response.recipe.RecipeSummaryDto;

import java.util.List;

public interface RecipeSummaryPopulationService {
    void populateRecipeSummaries(List<RecipeSummaryDto> recipes);
}
