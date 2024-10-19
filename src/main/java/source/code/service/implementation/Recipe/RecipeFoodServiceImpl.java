package source.code.service.implementation.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import source.code.dto.request.Recipe.RecipeFoodCreateDto;
import source.code.dto.response.FoodResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Food.Food;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.Recipe.RecipeFoodService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecipeFoodServiceImpl implements RecipeFoodService {
  private final ValidationServiceImpl validationServiceImpl;
  private final RecipeFoodRepository recipeFoodRepository;
  private final FoodRepository foodRepository;
  private final RecipeRepository recipeRepository;
  private final JsonPatchServiceImpl jsonPatchServiceImpl;
  private final FoodMapper foodMapper;

  public RecipeFoodServiceImpl(
          ValidationServiceImpl validationServiceImpl,
          RecipeFoodRepository recipeFoodRepository,
          FoodRepository foodRepository,
          RecipeRepository recipeRepository,
          JsonPatchServiceImpl jsonPatchServiceImpl,
          FoodMapper foodMapper) {
    this.validationServiceImpl = validationServiceImpl;
    this.recipeFoodRepository = recipeFoodRepository;
    this.foodRepository = foodRepository;
    this.recipeRepository = recipeRepository;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
    this.foodMapper = foodMapper;
  }

  @CacheEvict(value = "foodsByRecipe", key = "#recipeId")
  @Transactional
  public void saveFoodToRecipe(int recipeId, int foodId, RecipeFoodCreateDto request) {
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

  @CachePut(value = "foodsByRecipe", key = "#recipeId")
  @Transactional
  public void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    RecipeFood recipeFood = recipeFoodRepository
            .findByRecipeIdAndFoodId(recipeId, foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "RecipeFood with recipe id: " + recipeId
                            + " and food id: " + foodId + " not found"));

    RecipeFoodCreateDto existingRecipeFoodDto = new RecipeFoodCreateDto(recipeFood.getAmount());

    RecipeFoodCreateDto patchedDto = jsonPatchServiceImpl
            .applyPatch(patch, existingRecipeFoodDto, RecipeFoodCreateDto.class);

    validationServiceImpl.validate(patchedDto);

    if (patchedDto.getAmount() != recipeFood.getAmount()) {
      recipeFood.setAmount(patchedDto.getAmount());
    }

    recipeFoodRepository.save(recipeFood);
  }

  @CacheEvict(value = "foodsByRecipe", key = "#recipeId")
  @Transactional
  public void deleteFoodFromRecipe(int foodId, int recipeId) {
    RecipeFood recipeFood = recipeFoodRepository
            .findByRecipeIdAndFoodId(recipeId, foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "RecipeFood with recipe id: " + recipeId
                            + " and food id: " + foodId + " not found"));

    recipeFoodRepository.delete(recipeFood);
  }

  @Cacheable(value = "foodsByRecipe", key = "#recipeId")
  public List<FoodResponseDto> getFoodsByRecipe(int recipeId) {
    List<RecipeFood> recipeFoods = recipeFoodRepository.findByRecipeId(recipeId);

    List<Food> foods = recipeFoods.stream()
            .map(RecipeFood::getFood)
            .collect(Collectors.toList());

    return foods.stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  private boolean isAlreadyAdded(int recipeId, int foodId) {
    return recipeFoodRepository.existsByRecipeIdAndFoodId(recipeId, foodId);
  }
}