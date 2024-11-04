package source.code.service.Implementation.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Request.Recipe.RecipeCreateDto;
import source.code.dto.Request.Recipe.RecipeUpdateDto;
import source.code.dto.Response.RecipeResponseDto;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.mapper.Recipe.RecipeMapper;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.repository.RecipeCategoryRepository;
import source.code.repository.RecipeRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Recipe.RecipeService;
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
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeMapper recipeMapper,
                             JsonPatchService jsonPatchService,
                             ValidationService validationService,
                             ApplicationEventPublisher applicationEventPublisher,
                             RepositoryHelper repositoryHelper,
                             RecipeRepository recipeRepository,
                             RecipeCategoryRepository recipeCategoryRepository,
                             RecipeCategoryAssociationRepository recipeCategoryAssociationRepository) {
        this.recipeMapper = recipeMapper;
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.repositoryHelper = repositoryHelper;
        this.recipeRepository = recipeRepository;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
    }

    @Override
    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateDto request) {
        Recipe recipe = recipeRepository.save(recipeMapper.toEntity(request));
        applicationEventPublisher.publishEvent(new RecipeCreateEvent(this, recipe));

        return recipeMapper.toResponseDto(recipe);
    }

    @Override
    @Transactional
    public void updateRecipe(int recipeId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        Recipe recipe = find(recipeId);
        RecipeUpdateDto patchedRecipeUpdateDto = applyPatchToRecipe(recipe, patch);

        validationService.validate(patchedRecipeUpdateDto);

        recipeMapper.updateRecipe(recipe, patchedRecipeUpdateDto);
        Recipe savedRecipe = recipeRepository.save(recipe);

        applicationEventPublisher.publishEvent(new RecipeUpdateEvent(this, savedRecipe));
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeId) {
        Recipe recipe = find(recipeId);
        recipeRepository.delete(recipe);

        applicationEventPublisher.publishEvent(new RecipeDeleteEvent(this, recipe));
    }

    @Override
    @Cacheable(value = CacheNames.RECIPES, key = "#id")
    public RecipeResponseDto getRecipe(int id) {
        Recipe recipe = find(id);
        return recipeMapper.toResponseDto(recipe);
    }

    @Override
    @Cacheable(value = CacheNames.ALL_RECIPES)
    public List<RecipeResponseDto> getAllRecipes() {
        return repositoryHelper.findAll(recipeRepository, recipeMapper::toResponseDto);
    }

    @Override
    public List<RecipeResponseDto> getFilteredRecipes(FilterDto filter) {
        SpecificationFactory<Recipe> recipeFactory = RecipeSpecification::new;
        SpecificationBuilder<Recipe> specificationBuilder = SpecificationBuilder.create(filter, recipeFactory);
        Specification<Recipe> specification = specificationBuilder.build();

        return recipeRepository.findAll(specification).stream()
                .map(recipeMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<Recipe> getAllRecipeEntities() {
        return recipeRepository.findAllWithoutAssociations();
    }

    @Override
    @Cacheable(value = CacheNames.RECIPES_BY_CATEGORY, key = "#categoryId")
    public List<RecipeResponseDto> getRecipesByCategory(int categoryId) {
        return recipeCategoryAssociationRepository.findByRecipeCategoryId(categoryId).stream()
                .map(RecipeCategoryAssociation::getRecipe)
                .map(recipeMapper::toResponseDto)
                .toList();
    }

    private Recipe find(int recipeId) {
        return repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
    }

    private RecipeUpdateDto applyPatchToRecipe(Recipe recipe, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        RecipeResponseDto responseDto = recipeMapper.toResponseDto(recipe);
        return jsonPatchService.applyPatch(patch, responseDto, RecipeUpdateDto.class);
    }
}