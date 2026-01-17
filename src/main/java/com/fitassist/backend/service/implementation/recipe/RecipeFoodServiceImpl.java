package com.fitassist.backend.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.recipe.RecipeFoodCreateDto;
import com.fitassist.backend.dto.request.recipe.RecipeFoodUpdateDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.FoodMapper;
import com.fitassist.backend.mapper.recipe.RecipeFoodMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.recipe.RecipeFood;
import com.fitassist.backend.repository.FoodRepository;
import com.fitassist.backend.repository.RecipeFoodRepository;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.recipe.RecipeFoodService;
import com.fitassist.backend.service.declaration.recipe.RecipeService;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipeFoodServiceImpl implements RecipeFoodService {

	private final RecipeService recipeService;

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final FoodMapper foodMapper;

	private final RecipeFoodMapper recipeFoodMapper;

	private final RepositoryHelper repositoryHelper;

	private final RecipeFoodRepository recipeFoodRepository;

	private final FoodRepository foodRepository;

	private final RecipeRepository recipeRepository;

	private final FoodPopulationService foodPopulationService;

	public RecipeFoodServiceImpl(RecipeService recipeService, ValidationService validationService,
			RecipeFoodMapper recipeFoodMapper, RecipeFoodRepository recipeFoodRepository, FoodRepository foodRepository,
			RecipeRepository recipeRepository, JsonPatchService jsonPatchService, FoodMapper foodMapper,
			RepositoryHelper repositoryHelper, FoodPopulationService foodPopulationService) {
		this.recipeService = recipeService;
		this.validationService = validationService;
		this.recipeFoodMapper = recipeFoodMapper;
		this.recipeFoodRepository = recipeFoodRepository;
		this.foodRepository = foodRepository;
		this.recipeRepository = recipeRepository;
		this.jsonPatchService = jsonPatchService;
		this.foodMapper = foodMapper;
		this.repositoryHelper = repositoryHelper;
		this.foodPopulationService = foodPopulationService;
	}

	@Override
	@CacheEvict(value = { CacheNames.RECIPES }, key = "#recipeId")
	@Transactional
	public void saveFoodToRecipe(int recipeId, RecipeFoodCreateDto request) {
		List<Integer> foodIds = request.getFoods()
			.stream()
			.map(RecipeFoodCreateDto.FoodQuantityPair::getFoodId)
			.toList();

		validateNotYetAdded(recipeId, foodIds);

		Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);

		Map<Integer, Food> foodsMap = foodRepository.findAllById(foodIds)
			.stream()
			.collect(Collectors.toMap(Food::getId, food -> food));

		if (foodsMap.size() != foodIds.size()) {
			throw RecordNotFoundException.of(Food.class);
		}

		List<RecipeFood> recipeFoods = request.getFoods()
			.stream()
			.map(pair -> RecipeFood.of(pair.getQuantity(), recipe, foodsMap.get(pair.getFoodId())))
			.toList();

		recipeFoodRepository.saveAll(recipeFoods);
	}

	@Override
	@CacheEvict(value = { CacheNames.RECIPES }, key = "#recipeId")
	@Transactional
	public void replaceAllFoodsInRecipe(int recipeId, RecipeFoodCreateDto request) {
		Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
		recipe.getRecipeFoods().clear();

		List<Integer> foodIds = request.getFoods()
			.stream()
			.map(RecipeFoodCreateDto.FoodQuantityPair::getFoodId)
			.toList();

		Map<Integer, Food> foodsMap = foodRepository.findAllById(foodIds)
			.stream()
			.collect(Collectors.toMap(Food::getId, food -> food));

		if (foodsMap.size() != foodIds.size()) {
			throw RecordNotFoundException.of(Food.class);
		}

		request.getFoods()
			.stream()
			.map(pair -> RecipeFood.of(pair.getQuantity(), recipe, foodsMap.get(pair.getFoodId())))
			.forEach(recipe.getRecipeFoods()::add);

		recipeRepository.save(recipe);
	}

	private void validateNotYetAdded(int recipeId, List<Integer> foodIds) {
		List<RecipeFood> existingRecipeFoods = recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, foodIds);
		if (!existingRecipeFoods.isEmpty()) {
			int existingFoodId = existingRecipeFoods.getFirst().getFood().getId();
			throw new NotUniqueRecordException(RecipeFood.class, recipeId, existingFoodId);
		}
	}

	@Override
	@CacheEvict(value = { CacheNames.RECIPES }, key = "#recipeId")
	@Transactional
	public void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {

		RecipeFood recipeFood = find(recipeId, foodId);
		RecipeFoodUpdateDto patchedDto = jsonPatchService.createFromPatch(patch, RecipeFoodUpdateDto.class);

		validationService.validate(patchedDto);
		recipeFoodMapper.update(recipeFood, patchedDto);
		recipeFoodRepository.save(recipeFood);
	}

	@Override
	@CacheEvict(value = { CacheNames.RECIPES }, key = "#recipeId")
	@Transactional
	public void deleteFoodFromRecipe(int foodId, int recipeId) {
		RecipeFood recipeFood = find(recipeId, foodId);
		recipeFoodRepository.delete(recipeFood);
	}

	@Override
	public List<FoodSummaryDto> getFoodsByRecipe(int recipeId) {
		List<FoodSummaryDto> summaries = recipeFoodRepository.findByRecipeId(recipeId)
			.stream()
			.map(RecipeFood::getFood)
			.map(foodMapper::toSummaryDto)
			.toList();

		foodPopulationService.populate(summaries);

		return summaries;
	}

	private RecipeFood find(int recipeId, int foodId) {
		return recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)
			.orElseThrow(() -> RecordNotFoundException.of(RecipeFood.class, foodId, recipeId));
	}

}
