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
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
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
  private final JsonPatchServiceImpl jsonPatchServiceImpl;
  private final ValidationServiceImpl validationServiceImpl;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final RecipeRepository recipeRepository;
  private final UserRecipeRepository userRecipeRepository;
  private final RecipeCategoryRepository recipeCategoryRepository;
  private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;

  public RecipeServiceImpl(RecipeMapper recipeMapper,
                           JsonPatchServiceImpl jsonPatchServiceImpl,
                           ValidationServiceImpl validationServiceImpl,
                           ApplicationEventPublisher applicationEventPublisher,
                           RecipeRepository recipeRepository,
                           UserRecipeRepository userRecipeRepository,
                           RecipeCategoryRepository recipeCategoryRepository,
                           RecipeCategoryAssociationRepository recipeCategoryAssociationRepository) {
    this.recipeMapper = recipeMapper;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
    this.validationServiceImpl = validationServiceImpl;
    this.applicationEventPublisher = applicationEventPublisher;
    this.recipeRepository = recipeRepository;
    this.userRecipeRepository = userRecipeRepository;
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
    Recipe recipe = getRecipeOrThrow(recipeId);
    RecipeUpdateDto patchedRecipeUpdateDto = applyPatchToRecipe(recipe, patch);

    validationServiceImpl.validate(patchedRecipeUpdateDto);

    recipeMapper.updateRecipe(recipe, patchedRecipeUpdateDto);
    Recipe savedRecipe = recipeRepository.save(recipe);

    applicationEventPublisher.publishEvent(new RecipeUpdateEvent(this, savedRecipe));
  }

  @Transactional
  public void deleteRecipe(int recipeId) {
    Recipe recipe = getRecipeOrThrow(recipeId);
    recipeRepository.delete(recipe);

    applicationEventPublisher.publishEvent(new RecipeDeleteEvent(this, recipe));
  }

  @Cacheable(value = {"recipes"}, key = "#id")
  public RecipeResponseDto getRecipe(int id) {
    Recipe recipe = getRecipeOrThrow(id);
    return recipeMapper.toResponseDto(recipe);
  }

  @Cacheable(value = {"allRecipes"})
  public List<RecipeResponseDto> getAllRecipes() {
    List<Recipe> recipes = recipeRepository.findAll();

    return recipes.stream()
            .map(recipeMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"allRecipeCategories"})
  public List<RecipeCategoryResponseDto> getAllCategories() {
    List<RecipeCategory> categories = recipeCategoryRepository.findAll();

    return categories.stream()
            .map(recipeMapper::toCategoryDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"recipesByCategory"}, key = "#categoryId")
  public List<RecipeResponseDto> getRecipesByCategory(int categoryId) {
    List<RecipeCategoryAssociation> recipeCategoryAssociations =
            recipeCategoryAssociationRepository.findByRecipeCategoryId(categoryId);

    List<Recipe> recipes = recipeCategoryAssociations.stream()
            .map(RecipeCategoryAssociation::getRecipe)
            .collect(Collectors.toList());

    return recipes.stream()
            .map(recipeMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  private Recipe getRecipeOrThrow(int recipeId) {
    return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Recipe with id: " + recipeId + " not found"));
  }

  private RecipeUpdateDto applyPatchToRecipe(Recipe recipe, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    RecipeResponseDto responseDto = recipeMapper.toResponseDto(recipe);
    return jsonPatchServiceImpl.applyPatch(patch, responseDto, RecipeUpdateDto.class);
  }
}