package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Models.Recipe;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Models.UserRecipe;
import com.example.simplefullstackproject.Repositories.RecipeRepository;
import com.example.simplefullstackproject.Repositories.UserRecipeRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserRecipeService {
    private final ValidationHelper validationHelper;
    private final UserRecipeRepository userRecipeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public UserRecipeService(
            final ValidationHelper validationHelper,
            final UserRecipeRepository userRecipeRepository,
            final RecipeRepository recipeRepository,
            final UserRepository userRepository){
        this.validationHelper = validationHelper;
        this.userRecipeRepository = userRecipeRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addRecipeToUser(Integer recipeId, Integer userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + recipeId + " not found"));

        UserRecipe userRecipe = new UserRecipe();
        userRecipe.setUser(user);
        userRecipe.setRecipe(recipe);
        userRecipeRepository.save(userRecipe);
    }

    @Transactional
    public void deleteRecipeFromUser(Integer recipeId, Integer userId) {
        UserRecipe userRecipe = userRecipeRepository
                .findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserRecipe with user id: " + userId +
                                " and recipe id: " + recipeId + " not found"));

        userRecipeRepository.delete(userRecipe);
    }


}