package source.code.service.implementation.helpers;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.RecipeSummaryPopulationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipeSummaryPopulationServiceImpl implements RecipeSummaryPopulationService {
    private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
    private final ImageUrlPopulationService imageUrlPopulationService;

    public RecipeSummaryPopulationServiceImpl(
            RecipeCategoryAssociationRepository recipeCategoryAssociationRepository,
            ImageUrlPopulationService imageUrlPopulationService) {
        this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
        this.imageUrlPopulationService = imageUrlPopulationService;
    }

    @Override
    public void populateRecipeSummaries(List<RecipeSummaryDto> recipes) {
        if (recipes.isEmpty()) return;

        List<Integer> recipeIds = recipes.stream()
                .map(RecipeSummaryDto::getId)
                .toList();

        Map<Integer, List<RecipeCategoryShortDto>> categoriesMap = fetchCategoriesForRecipes(recipeIds);
        recipes.forEach(recipe -> recipe.setCategories(
                categoriesMap.getOrDefault(recipe.getId(), new ArrayList<>())
        ));

        imageUrlPopulationService.populateAuthorAndEntityImagesForList(
                recipes,
                RecipeSummaryDto::getAuthorImageName,
                RecipeSummaryDto::setAuthorImageUrl,
                RecipeSummaryDto::getFirstImageName,
                RecipeSummaryDto::setFirstImageUrl
        );
    }

    private Map<Integer, List<RecipeCategoryShortDto>> fetchCategoriesForRecipes(List<Integer> recipeIds) {
        return recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(recipeIds).stream()
                .collect(Collectors.groupingBy(
                        arr -> (Integer) arr[0],
                        Collectors.mapping(
                                arr -> new RecipeCategoryShortDto((Integer) arr[1], (String) arr[2]),
                                Collectors.toList()
                        )
                ));
    }
}
