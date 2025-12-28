package source.code.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.util.List;

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
	@CacheEvict(value = { CacheNames.FOODS_BY_RECIPE }, allEntries = true)
	@Transactional
	public void saveFoodToRecipe(int recipeId, RecipeFoodCreateDto request) {
		List<Integer> foodIds = request.getFoodIds();

		isAlreadyAdded(recipeId, foodIds);

		Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
		List<Food> foods = foodRepository.findAllById(foodIds);

		if (foods.size() != foodIds.size()) {
			throw RecordNotFoundException.of(Food.class);
		}

		List<RecipeFood> recipeFoods = foods.stream()
			.map(food -> RecipeFood.of(request.getQuantity(), recipe, food))
			.toList();

		recipeFoodRepository.saveAll(recipeFoods);
	}

	private void isAlreadyAdded(int recipeId, List<Integer> foodIds) {
		List<RecipeFood> existingRecipeFoods = recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, foodIds);
		if (!existingRecipeFoods.isEmpty()) {
			int existingFoodId = existingRecipeFoods.getFirst().getFood().getId();
			throw new NotUniqueRecordException(RecipeFood.class, recipeId, existingFoodId);
		}
	}

	@Override
	@CachePut(value = { CacheNames.FOODS_BY_RECIPE }, key = "#recipeId")
	@Transactional
	public void updateFoodRecipe(int recipeId, int foodId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {

		RecipeFood recipeFood = find(recipeId, foodId);
		RecipeFoodCreateDto patchedDto = jsonPatchService.createFromPatch(patch, RecipeFoodCreateDto.class);

		validationService.validate(patchedDto);
		recipeFoodMapper.update(recipeFood, patchedDto);
		recipeFoodRepository.save(recipeFood);
	}

	@Override
	@CacheEvict(value = { CacheNames.FOODS_BY_RECIPE }, key = "#recipeId")
	@Transactional
	public void deleteFoodFromRecipe(int foodId, int recipeId) {
		RecipeFood recipeFood = find(recipeId, foodId);
		recipeFoodRepository.delete(recipeFood);
	}

	@Override
	@Cacheable(value = CacheNames.FOODS_BY_RECIPE, key = "#recipeId")
	public List<FoodSummaryDto> getFoodsByRecipe(int recipeId) {
		List<FoodSummaryDto> summaries = recipeFoodRepository.findByRecipeId(recipeId)
			.stream()
			.map(RecipeFood::getFood)
			.map(foodMapper::toSummaryDto)
			.toList();

		foodPopulationService.populate(summaries);

		return summaries;
	}

	@Override
	public Page<RecipeSummaryDto> getRecipesByFoods(FilterRecipesByFoodsDto filter, Pageable pageable) {
		List<FilterCriteria> foodCriteriaList = filter.getFoodIds()
			.stream()
			.map(foodId -> FilterCriteria.of("FOODS", foodId, FilterOperation.EQUAL))
			.toList();

		return recipeService.getFilteredRecipes(FilterDto.of(foodCriteriaList, FilterDataOption.AND), pageable);
	}

	private RecipeFood find(int recipeId, int foodId) {
		return recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)
			.orElseThrow(() -> RecordNotFoundException.of(RecipeFood.class, foodId, recipeId));
	}

	private boolean isAlreadyAdded(int recipeId, int foodId) {
		return recipeFoodRepository.existsByRecipeIdAndFoodId(recipeId, foodId);
	}
}
