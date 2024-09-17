package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.LikesAndSavedDto;
import com.example.simplefullstackproject.exception.NotUniqueRecordException;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.model.Recipe;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.model.UserRecipe;
import com.example.simplefullstackproject.repository.RecipeRepository;
import com.example.simplefullstackproject.repository.UserRecipeRepository;
import com.example.simplefullstackproject.repository.UserRepository;
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
    public void saveRecipeToUser(int recipeId, int userId, short type) {
        if(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId +
                            " already has recipe with id: " + recipeId +
                            " and type: " + type);
        }

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
    public void deleteSavedRecipeFromUser(int recipeId, int userId, short type) {
        UserRecipe userRecipe = userRecipeRepository
                .findByUserIdAndRecipeIdAndType(userId, recipeId, type)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserRecipe with user id: " + userId +
                                ", recipe id: " + recipeId +
                                " and type: " + type + " not found"));

        userRecipeRepository.delete(userRecipe);
    }

    public LikesAndSavedDto calculateLikesAndSavesByRecipeId(int recipeId) {
        recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + recipeId + " not found"));

        long saves = userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 1);
        long likes = userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 2);

        return new LikesAndSavedDto(likes, saves);
    }
}