package source.code.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.FilterRecipesByFoodsDto;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.dto.request.recipe.RecipeFoodUpdateDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.Enum.filter.FilterDataOption;
import source.code.helper.Enum.filter.FilterOperation;
import source.code.mapper.FoodMapper;
import source.code.mapper.recipe.RecipeFoodMapper;
import source.code.model.food.Food;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.food.FoodPopulationService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipeFoodService;
import source.code.service.declaration.recipe.RecipeService;

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
