package source.code.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.declaration.recipe.RecipeService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.RecipeSpecification;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeMapper recipeMapper;
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RepositoryHelper repositoryHelper;
    private final RecipeRepository recipeRepository;
    private final RecipePopulationService recipePopulationService;
    private final SpecificationDependencies dependencies;

    public RecipeServiceImpl(RecipeMapper recipeMapper,
                             JsonPatchService jsonPatchService,
                             ValidationService validationService,
                             ApplicationEventPublisher applicationEventPublisher,
                             RepositoryHelper repositoryHelper,
                             RecipeRepository recipeRepository,
                             RecipePopulationService recipePopulationService,
                             SpecificationDependencies dependencies) {
        this.recipeMapper = recipeMapper;
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.repositoryHelper = repositoryHelper;
        this.recipeRepository = recipeRepository;
        this.recipePopulationService = recipePopulationService;
        this.dependencies = dependencies;
    }

    @Override
    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateDto request) {
        int userId = AuthorizationUtil.getUserId();
        Recipe mapped = recipeMapper.toEntity(request, userId);
        Recipe saved = recipeRepository.save(mapped);
        applicationEventPublisher.publishEvent(RecipeCreateEvent.of(this, saved));

        recipeRepository.flush();

        return findAndMap(saved.getId());
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
        return findAndMap(id);
    }

    @Override
    public Page<RecipeSummaryDto> getAllRecipes(Boolean showPrivate, Pageable pageable) {
        int userId = AuthorizationUtil.getUserId();

        Page<Recipe> recipePage = recipeRepository.findAllWithDetails(showPrivate, userId, pageable);
        List<RecipeSummaryDto> summaries = recipePage.getContent().stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
        recipePopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, recipePage.getTotalElements());
    }

    @Override
    public Page<RecipeSummaryDto> getFilteredRecipes(FilterDto filter, Pageable pageable) {
        SpecificationFactory<Recipe> recipeFactory = RecipeSpecification::of;
        SpecificationBuilder<Recipe> specificationBuilder = SpecificationBuilder.of(filter, recipeFactory, dependencies);
        Specification<Recipe> specification = specificationBuilder.build();

        Page<Recipe> recipePage = recipeRepository.findAll(specification, pageable);
        List<RecipeSummaryDto> summaries = recipePage.getContent().stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
        recipePopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, recipePage.getTotalElements());
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

    private RecipeResponseDto findAndMap(int recipeId) {
        Recipe recipe = recipeRepository.findByIdWithDetails(recipeId)
                .orElseThrow(() -> RecordNotFoundException.of(Recipe.class, recipeId));

        RecipeResponseDto dto = recipeMapper.toResponseDto(recipe);
        recipePopulationService.populate(dto);
        return dto;
    }

    private RecipeUpdateDto applyPatchToRecipe(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        return jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class);
    }
}