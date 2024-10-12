package source.code.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.RecipeFoodCreateDto;
import source.code.exception.NotUniqueRecordException;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.model.Food.Food;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.RecipeFoodService;

import java.util.NoSuchElementException;

@Service
public class RecipeFoodServiceImpl implements RecipeFoodService {
  private final ValidationHelper validationHelper;
  private final RecipeFoodRepository recipeFoodRepository;
  private final FoodRepository foodRepository;
  private final RecipeRepository recipeRepository;
  private final JsonPatchHelper jsonPatchHelper;

  public RecipeFoodServiceImpl(
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
    if(isAlreadyAdded(recipeId, foodId)) {
      throw new NotUniqueRecordException(
              "Recipe with id: " + recipeId
              + " already has food with id: " + foodId);
    }

    Recipe recipe = recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Recipe with id: " + recipeId + " not found"));

    Food food = foodRepository
            .findById(foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + foodId + " not found"));

    RecipeFood recipeFood = RecipeFood
            .createWithAmountRecipeFood(request.getAmount(), recipe, food);

    recipeFoodRepository.save(recipeFood);
  }

  private boolean isAlreadyAdded(int recipeId, int foodId) {
    return recipeFoodRepository.existsByRecipeIdAndFoodId(recipeId, foodId);
  }

  @Transactional
  public void deleteFoodFromRecipe(int foodId, int recipeId) {
    RecipeFood recipeFood = recipeFoodRepository
            .findByRecipeIdAndFoodId(recipeId, foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "RecipeFood with recipe id: " + recipeId
                            + " and food id: " + foodId + " not found"));

    recipeFoodRepository.delete(recipeFood);
  }

  @Transactional
  public void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    RecipeFood recipeFood = recipeFoodRepository
            .findByRecipeIdAndFoodId(recipeId, foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "RecipeFood with recipe id: " + recipeId
                            + " and food id: " + foodId + " not found"));

    RecipeFoodCreateDto existingRecipeFoodDto = new RecipeFoodCreateDto(recipeFood.getAmount());

    RecipeFoodCreateDto patchedDto = jsonPatchHelper
            .applyPatch(
                    patch,
                    existingRecipeFoodDto,
                    RecipeFoodCreateDto.class);

    validationHelper.validate(patchedDto);

    if (patchedDto.getAmount() != recipeFood.getAmount()) {
      recipeFood.setAmount(patchedDto.getAmount());
    }

    recipeFoodRepository.save(recipeFood);
  }
}