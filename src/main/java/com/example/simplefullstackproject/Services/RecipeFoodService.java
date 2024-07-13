package com.example.simplefullstackproject.Services;

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
    private final RecipeFoodRepository recipeFoodRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;

    public RecipeFoodService(
            final RecipeFoodRepository recipeFoodRepository,
            final FoodRepository foodRepository,
            final RecipeRepository recipeRepository) {
        this.recipeFoodRepository = recipeFoodRepository;
        this.foodRepository = foodRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public void addFoodToRecipe(Integer foodId, Integer recipeId) {
        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Recipe with id: " + recipeId + " not found"));

        Food food = foodRepository
                .findById(foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Food with id: " + foodId + " not found"));

        RecipeFood recipeFood = new RecipeFood();
        recipeFood.setRecipe(recipe);
        recipeFood.setFood(food);
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
}