package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.RecipeDto;
import com.example.simplefullstackproject.models.Recipe;
import com.example.simplefullstackproject.models.UserRecipe;
import com.example.simplefullstackproject.repositories.RecipeRepository;
import com.example.simplefullstackproject.repositories.UserRecipeRepository;
import com.example.simplefullstackproject.repositories.UserRepository;
import com.example.simplefullstackproject.services.Mappers.RecipeDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final ValidationHelper validationHelper;
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserRecipeRepository userRecipeRepository;

    public RecipeService(
            ValidationHelper validationHelper,
            RecipeDtoMapper recipeDtoMapper,
            RecipeRepository recipeRepository,
            UserRepository userRepository,
            UserRecipeRepository userRecipeRepository) {
        this.validationHelper = validationHelper;
        this.recipeDtoMapper = recipeDtoMapper;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userRecipeRepository = userRecipeRepository;
    }

    @Transactional
    public RecipeDto save(RecipeDto request) {
        validationHelper.validate(request);

        Recipe recipe = recipeRepository.save(recipeDtoMapper.map(request));
        return recipeDtoMapper.map(recipe);
    }

    public RecipeDto getRecipeById(Integer id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + id + " not found"));
        return recipeDtoMapper.map(recipe);
    }

    public List<RecipeDto> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipeDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<RecipeDto> getRecipesByUserID(Integer userId) {
        List<UserRecipe> userRecipes = userRecipeRepository.findByUserId(userId);
        List<Recipe> recipes = userRecipes.stream()
                .map(UserRecipe::getRecipe)
                .collect(Collectors.toList());
        return recipes.stream()
                .map(recipeDtoMapper::map)
                .collect(Collectors.toList());
    }
}