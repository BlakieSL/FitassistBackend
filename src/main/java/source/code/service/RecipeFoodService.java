package source.code.service;

import source.code.dto.request.RecipeFoodCreateDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.model.Food;
import source.code.model.Recipe;
import source.code.model.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
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
    public void addFoodToRecipe(int recipeId, int foodId, RecipeFoodCreateDto request) {
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

        RecipeFoodCreateDto existingRecipeFoodDto = new RecipeFoodCreateDto(
                recipeFood.getAmount()
        );

        RecipeFoodCreateDto patchedDto = jsonPatchHelper.applyPatch(patch, existingRecipeFoodDto, RecipeFoodCreateDto.class);

        validationHelper.validate(patchedDto);

        if (patchedDto.getAmount() != recipeFood.getAmount()) {
            recipeFood.setAmount(patchedDto.getAmount());
        }

        recipeFoodRepository.save(recipeFood);
    }
}