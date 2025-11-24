package source.code.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RecipeSummaryPopulationService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipeService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.RecipeSpecification;

import java.util.Collections;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeMapper recipeMapper;
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RepositoryHelper repositoryHelper;
    private final RecipeRepository recipeRepository;
    private final RecipeSummaryPopulationService recipeSummaryPopulationService;
    private final SpecificationDependencies dependencies;

    public RecipeServiceImpl(RecipeMapper recipeMapper,
                             JsonPatchService jsonPatchService,
                             ValidationService validationService,
                             ApplicationEventPublisher applicationEventPublisher,
                             RepositoryHelper repositoryHelper,
                             RecipeRepository recipeRepository,
                             RecipeSummaryPopulationService recipeSummaryPopulationService,
                             SpecificationDependencies dependencies) {
        this.recipeMapper = recipeMapper;
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.repositoryHelper = repositoryHelper;
        this.recipeRepository = recipeRepository;
        this.recipeSummaryPopulationService = recipeSummaryPopulationService;
        this.dependencies = dependencies;
    }

    @Override
    @Transactional
    public RecipeSummaryDto createRecipe(RecipeCreateDto request) {
        int userId = AuthorizationUtil.getUserId();
        Recipe mapped = recipeMapper.toEntity(request, userId);
        Recipe saved = recipeRepository.save(mapped);
        applicationEventPublisher.publishEvent(RecipeCreateEvent.of(this, saved));

        recipeRepository.flush();

        List<RecipeSummaryDto> summaries = recipeRepository.findRecipeSummariesByIds(List.of(saved.getId()));
        recipeSummaryPopulationService.populateRecipeSummaries(summaries);
        return summaries.getFirst();
    }

    @Override
    @Transactional
    public void updateRecipe(int recipeId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Recipe recipe = find(recipeId);
        RecipeUpdateDto patchedRecipeUpdateDto = applyPatchToRecipe(patch);

        validationService.validate(patchedRecipeUpdateDto);
        recipeMapper.updateRecipe(recipe, patchedRecipeUpdateDto);
        Recipe savedRecipe = recipeRepository.save(recipe);

        applicationEventPublisher.publishEvent(RecipeUpdateEvent.of(this, savedRecipe));
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeId) {
        Recipe recipe = find(recipeId);
        recipeRepository.delete(recipe);

        applicationEventPublisher.publishEvent(RecipeDeleteEvent.of(this, recipe));
    }

    @Override
    @Cacheable(value = CacheNames.RECIPES, key = "#id")
    public RecipeResponseDto getRecipe(int id) {
        Recipe recipe = find(id);
        return recipeMapper.toResponseDto(recipe);
    }

    @Override
    @Cacheable(value = CacheNames.ALL_RECIPES)
    public List<RecipeSummaryDto> getAllRecipes(Boolean isPrivate) {
        int userId = AuthorizationUtil.getUserId();
        List<RecipeSummaryDto> recipes = recipeRepository.findAllRecipeSummaries(isPrivate, userId);
        recipeSummaryPopulationService.populateRecipeSummaries(recipes);
        return recipes;
    }

    @Override
    public List<RecipeSummaryDto> getFilteredRecipes(FilterDto filter) {
        SpecificationFactory<Recipe> recipeFactory = RecipeSpecification::of;
        SpecificationBuilder<Recipe> specificationBuilder = SpecificationBuilder.of(filter, recipeFactory, dependencies);
        Specification<Recipe> specification = specificationBuilder.build();

        List<Integer> recipeIds = recipeRepository.findAll(specification).stream()
                .map(Recipe::getId)
                .toList();

        if (recipeIds.isEmpty()) return List.of();

        List<RecipeSummaryDto> recipes = recipeRepository.findRecipeSummariesByIds(recipeIds);
        recipeSummaryPopulationService.populateRecipeSummaries(recipes);

        return recipes;
    }

    @Override
    public List<Recipe> getAllRecipeEntities() {
        return recipeRepository.findAllWithoutAssociations();
    }

    @Override
    @Transactional
    public void incrementViews(int recipeId) {
        recipeRepository.incrementViews(recipeId);
    }

    private Recipe find(int recipeId) {
        return repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
    }

    private RecipeUpdateDto applyPatchToRecipe(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        return jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class);
    }
}