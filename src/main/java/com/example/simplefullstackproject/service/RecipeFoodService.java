package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.AddFoodRecipeDto;
import com.example.simplefullstackproject.helper.JsonPatchHelper;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.model.Food;
import com.example.simplefullstackproject.model.Recipe;
import com.example.simplefullstackproject.model.RecipeFood;
import com.example.simplefullstackproject.repository.FoodRepository;
import com.example.simplefullstackproject.repository.RecipeFoodRepository;
import com.example.simplefullstackproject.repository.RecipeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RecipeFoodService {
    private final ValidationHelper validationHelper;
    private final RecipeFoodRepository recipeFoodRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final JsonPatchHelper jsonPatchHelper;

    public RecipeFoodService(
            final ValidationHelper validationHelper,
            final RecipeFoodRepository recipeFoodRepository,
            final FoodRepository foodRepository,
            final RecipeRepository recipeRepository, JsonPatchHelper jsonPatchHelper) {
        this.validationHelper = validationHelper;
        this.recipeFoodRepository = recipeFoodRepository;
        this.foodRepository = foodRepository;
        this.recipeRepository = recipeRepository;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    @Transactional
    public void addFoodToRecipe(int recipeId, int foodId, AddFoodRecipeDto request) {
        validationHelper.validate(request);

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
        recipeFood.setAmount(request.getAmount());
        recipeFoodRepository.save(recipeFood);
    }

    @Transactional
    public void deleteFoodFromRecipe(int foodId, int recipeId) {
        RecipeFood recipeFood = recipeFoodRepository
                .findByRecipeIdAndFoodId(recipeId, foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "RecipeFood with recipe id: " + recipeId +
                                " and food id: " + foodId + " not found"));

        recipeFoodRepository.delete(recipeFood);
    }

    @Transactional
    public void modifyFoodRecipe(int recipeId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        RecipeFood recipeFood = recipeFoodRepository
                .findByRecipeIdAndFoodId(recipeId, foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "RecipeFood with recipe id: " + recipeId + " and food id: " + foodId + " not found"));

        AddFoodRecipeDto existingRecipeFoodDto = new AddFoodRecipeDto(
                recipeFood.getAmount()
        );

        AddFoodRecipeDto patchedDto = jsonPatchHelper.applyPatch(patch, existingRecipeFoodDto, AddFoodRecipeDto.class);

        validationHelper.validate(patchedDto);

        if (patchedDto.getAmount() != recipeFood.getAmount()) {
            recipeFood.setAmount(patchedDto.getAmount());
        }

        recipeFoodRepository.save(recipeFood);
    }
}