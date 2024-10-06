package source.code.service;

import source.code.dto.RecipeAdditionDto;
import source.code.dto.RecipeCategoryDto;
import source.code.dto.RecipeDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.RecipeMapper;
import source.code.model.Recipe;
import source.code.model.RecipeCategory;
import source.code.model.RecipeCategoryAssociation;
import source.code.model.UserRecipe;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.repository.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final ValidationHelper validationHelper;
    private final RecipeMapper recipeMapper;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
    public RecipeService(
            ValidationHelper validationHelper,
            RecipeMapper recipeMapper,
            RecipeRepository recipeRepository,
            UserRepository userRepository,
            UserRecipeRepository userRecipeRepository,
            RecipeCategoryRepository recipeCategoryRepository, RecipeCategoryAssociationRepository recipeCategoryAssociationRepository) {
        this.validationHelper = validationHelper;
        this.recipeMapper = recipeMapper;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userRecipeRepository = userRecipeRepository;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
    }

    @Transactional
    public RecipeDto save(RecipeAdditionDto dto) {
        validationHelper.validate(dto);

        Recipe recipe = recipeRepository.save(recipeMapper.toEntity(dto));
        return recipeMapper.toDto(recipe);
    }

    public RecipeDto getRecipeById(int id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + id + " not found"));
        return recipeMapper.toDto(recipe);
    }

    public List<RecipeDto> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RecipeDto> getRecipesByUserID(int userId) {
        List<UserRecipe> userRecipes = userRecipeRepository.findByUserId(userId);
        List<Recipe> recipes = userRecipes.stream()
                .map(UserRecipe::getRecipe)
                .collect(Collectors.toList());
        return recipes.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RecipeCategoryDto> getCategories() {
        List<RecipeCategory> categories = recipeCategoryRepository.findAll();
        return categories.stream()
                .map(recipeMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<RecipeDto> getRecipesByCategory(int categoryId) {
        List<RecipeCategoryAssociation> recipeCategoryAssociations = recipeCategoryAssociationRepository.findByRecipeCategoryId(categoryId);
        List<Recipe> recipes = recipeCategoryAssociations.stream()
                .map(RecipeCategoryAssociation::getRecipe)
                .collect(Collectors.toList());
        return recipes.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }
}