package source.code.service.implementation.Recipe;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Recipe.RecipeCreateEvent;
import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.mapper.RecipeMapper;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategory;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.model.User.UserRecipe;
import source.code.repository.*;
import source.code.service.declaration.RecipeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
  private final RecipeMapper recipeMapper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final RecipeRepository recipeRepository;
  private final UserRecipeRepository userRecipeRepository;
  private final RecipeCategoryRepository recipeCategoryRepository;
  private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;

  public RecipeServiceImpl(RecipeMapper recipeMapper,
                           ApplicationEventPublisher applicationEventPublisher,
                           RecipeRepository recipeRepository,
                           UserRecipeRepository userRecipeRepository,
                           RecipeCategoryRepository recipeCategoryRepository,
                           RecipeCategoryAssociationRepository recipeCategoryAssociationRepository) {
    this.recipeMapper = recipeMapper;
    this.applicationEventPublisher = applicationEventPublisher;
    this.recipeRepository = recipeRepository;
    this.userRecipeRepository = userRecipeRepository;
    this.recipeCategoryRepository = recipeCategoryRepository;
    this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
  }

  @Transactional
  public RecipeResponseDto createRecipe(RecipeCreateDto request) {
    Recipe recipe = recipeRepository.save(recipeMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new RecipeCreateEvent(this, request));

    return recipeMapper.toDto(recipe);
  }

  @Cacheable(value = {"recipes"}, key = "#id")
  public RecipeResponseDto getRecipe(int id) {
    Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Recipe with id: " + id + " not found"));

    return recipeMapper.toDto(recipe);
  }

  @Cacheable(value = {"allRecipes"})
  public List<RecipeResponseDto> getAllRecipes() {
    List<Recipe> recipes = recipeRepository.findAll();

    return recipes.stream()
            .map(recipeMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<RecipeResponseDto> getRecipesByUserAndType(int userId, short type) {
    List<UserRecipe> userRecipes = userRecipeRepository.findByUserIdAndType(userId, type);

    List<Recipe> recipes = userRecipes.stream()
            .map(UserRecipe::getRecipe)
            .collect(Collectors.toList());

    return recipes.stream()
            .map(recipeMapper::toDto)
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
            .map(recipeMapper::toDto)
            .collect(Collectors.toList());
  }
}