package source.code.service.implementation.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Recipe.RecipeCreateEvent;
import source.code.cache.event.Recipe.RecipeDeleteEvent;
import source.code.cache.event.Recipe.RecipeUpdateEvent;
import source.code.dto.request.Recipe.RecipeCreateDto;
import source.code.dto.request.Recipe.RecipeUpdateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.mapper.Recipe.RecipeMapper;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategory;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.repository.*;
import source.code.service.declaration.Recipe.RecipeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

  @Transactional
  public RecipeResponseDto createRecipe(RecipeCreateDto request) {
    Recipe recipe = recipeRepository.save(recipeMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new RecipeCreateEvent(this, recipe));

    return recipeMapper.toResponseDto(recipe);
  }

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

  @Transactional
  public void deleteRecipe(int recipeId) {
    Recipe recipe = find(recipeId);
    recipeRepository.delete(recipe);

    applicationEventPublisher.publishEvent(new RecipeDeleteEvent(this, recipe));
  }

  @Cacheable(value = {"recipes"}, key = "#id")
  public RecipeResponseDto getRecipe(int id) {
    Recipe recipe = find(id);
    return recipeMapper.toResponseDto(recipe);
  }

  @Cacheable(value = {"allRecipes"})
  public List<RecipeResponseDto> getAllRecipes() {
    return repositoryHelper.findAll(recipeRepository, recipeMapper::toResponseDto);
  }

  @Cacheable(value = {"allRecipeCategories"})
  public List<RecipeCategoryResponseDto> getAllCategories() {
    return repositoryHelper.findAll(recipeCategoryRepository, recipeMapper::toCategoryDto);
  }

  @Cacheable(value = {"recipesByCategory"}, key = "#categoryId")
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