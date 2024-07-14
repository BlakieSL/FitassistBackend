package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.AddFoodRecipeDto;
import com.example.simplefullstackproject.Models.Food;
import com.example.simplefullstackproject.Models.Recipe;
import com.example.simplefullstackproject.Models.RecipeFood;
import com.example.simplefullstackproject.Repositories.FoodRepository;
import com.example.simplefullstackproject.Repositories.RecipeFoodRepository;
import com.example.simplefullstackproject.Repositories.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RecipeFoodService {
    private final ValidationHelper validationHelper;
    private final RecipeFoodRepository recipeFoodRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;

    public RecipeFoodService(
            final ValidationHelper validationHelper,
            final RecipeFoodRepository recipeFoodRepository,
            final FoodRepository foodRepository,
            final RecipeRepository recipeRepository) {
        this.validationHelper = validationHelper;
        this.recipeFoodRepository = recipeFoodRepository;
        this.foodRepository = foodRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public void addFoodToRecipe(Integer recipeId, AddFoodRecipeDto request) {
        validationHelper.validate(request);

        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + recipeId + " not found"));

        Food food = foodRepository
                .findById(request.getFoodId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Food with id: " + request.getFoodId() + " not found"));

        RecipeFood recipeFood = new RecipeFood();
        recipeFood.setRecipe(recipe);
        recipeFood.setFood(food);
        recipeFood.setAmount(request.getAmount());
        recipeFoodRepository.save(recipeFood);
    }

    @Transactional
    public void deleteFoodFromRecipe(Integer foodId, Integer recipeId) {
        RecipeFood recipeFood = recipeFoodRepository
                .findByRecipeIdAndFoodId(recipeId, foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "RecipeFood with recipe id: " + recipeId +
                                " and food id: " + foodId + " not found"));

        recipeFoodRepository.delete(recipeFood);
    }

    @Transactional
    public void modifyFoodRecipe(Integer recipeId, AddFoodRecipeDto request) {
        validationHelper.validate(request);

        RecipeFood recipeFood = recipeFoodRepository
                .findByRecipeIdAndFoodId(recipeId, request.getFoodId())
                .orElseThrow(() -> new NoSuchElementException(
                        "RecipeFood with recipe id: " + recipeId +
                                " and food id: " + request.getFoodId() + " not found"));

        if (request.getAmount() != recipeFood.getAmount()) {
            recipeFood.setAmount(request.getAmount());
        }
        recipeFoodRepository.save(recipeFood);
    }
}