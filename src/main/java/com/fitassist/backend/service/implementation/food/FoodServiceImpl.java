package com.fitassist.backend.service.implementation.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.food.CalculateFoodMacrosRequestDto;
import com.fitassist.backend.dto.request.food.FoodCreateDto;
import com.fitassist.backend.dto.request.food.FoodUpdateDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.event.event.Food.FoodCreateEvent;
import com.fitassist.backend.event.event.Food.FoodDeleteEvent;
import com.fitassist.backend.event.event.Food.FoodUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.mapper.food.FoodMappingContext;
import com.fitassist.backend.mapper.recipe.RecipeMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.food.FoodCategory;
import com.fitassist.backend.repository.FoodCategoryRepository;
import com.fitassist.backend.repository.FoodRepository;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.declaration.food.FoodService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.recipe.RecipePopulationService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.FoodSpecification;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final FoodMapper foodMapper;

	private final RecipeMapper recipeMapper;

	private final RepositoryHelper repositoryHelper;

	private final FoodRepository foodRepository;

	private final FoodCategoryRepository foodCategoryRepository;

	private final RecipeRepository recipeRepository;

	private final FoodPopulationService foodPopulationService;

	private final RecipePopulationService recipePopulationService;

	private final SpecificationDependencies dependencies;

	public FoodServiceImpl(ApplicationEventPublisher applicationEventPublisher, ValidationService validationService,
			JsonPatchService jsonPatchService, FoodRepository foodRepository,
			FoodCategoryRepository foodCategoryRepository, RecipeRepository recipeRepository, FoodMapper foodMapper,
			RecipeMapper recipeMapper, RepositoryHelper repositoryHelper, FoodPopulationService foodPopulationService,
			RecipePopulationService recipePopulationService, SpecificationDependencies dependencies) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.foodRepository = foodRepository;
		this.foodCategoryRepository = foodCategoryRepository;
		this.recipeRepository = recipeRepository;
		this.foodMapper = foodMapper;
		this.recipeMapper = recipeMapper;
		this.repositoryHelper = repositoryHelper;
		this.foodPopulationService = foodPopulationService;
		this.recipePopulationService = recipePopulationService;
		this.dependencies = dependencies;
	}

	@Override
	@Transactional
	public FoodResponseDto createFood(FoodCreateDto request) {
		FoodMappingContext context = prepareCreateContext(request);
		Food saved = foodRepository.save(foodMapper.toEntity(request, context));

		foodRepository.flush();

		Food food = foodRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Food.class, saved.getId()));

		applicationEventPublisher.publishEvent(FoodCreateEvent.of(this, food));

		FoodResponseDto dto = foodMapper.toDetailedResponseDto(food);
		foodPopulationService.populate(dto);
		return dto;
	}

	private FoodMappingContext prepareCreateContext(FoodCreateDto dto) {
		FoodCategory category = findCategory(dto.getCategoryId());
		return new FoodMappingContext(category);
	}

	@Override
	@Transactional
	public void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Food food = find(foodId);
		FoodUpdateDto patchedFoodUpdateDto = applyPatchToFood(patch);

		validationService.validate(patchedFoodUpdateDto);

		FoodMappingContext context = prepareUpdateContext(patchedFoodUpdateDto);
		foodMapper.updateFood(food, patchedFoodUpdateDto, context);
		Food saved = foodRepository.save(food);

		foodRepository.flush();

		Food refetchedFood = foodRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Food.class, saved.getId()));

		applicationEventPublisher.publishEvent(FoodUpdateEvent.of(this, refetchedFood));
	}

	private FoodMappingContext prepareUpdateContext(FoodUpdateDto dto) {
		FoodCategory category = findCategory(dto.getCategoryId());
		return new FoodMappingContext(category);
	}

	@Override
	@Transactional
	public void deleteFood(int foodId) {
		Food food = find(foodId);
		foodRepository.delete(food);

		applicationEventPublisher.publishEvent(FoodDeleteEvent.of(this, food));
	}

	@Override
	public FoodCalculatedMacrosResponseDto calculateFoodMacros(int id, CalculateFoodMacrosRequestDto request) {
		Food food = find(id);
		BigDecimal quantity = request.getQuantity();
		BigDecimal divisor = BigDecimal.valueOf(100);

		BigDecimal factor = quantity.divide(divisor, 10, RoundingMode.HALF_UP);

		return foodMapper.toDtoWithFactor(food, factor);
	}

	@Override
	@Cacheable(value = CacheNames.FOODS, key = "#id")
	public FoodResponseDto getFood(int id) {
		FoodResponseDto dto = findAndMap(id);

		List<RecipeSummaryDto> summaries = recipeRepository.findAllWithDetailsByFoodId(id)
			.stream()
			.map(recipeMapper::toSummaryDto)
			.toList();
		recipePopulationService.populate(summaries);
		dto.setRecipes(summaries);

		return dto;
	}

	@Override
	public Page<FoodSummaryDto> getFilteredFoods(FilterDto filter, Pageable pageable) {
		SpecificationFactory<Food> foodFactory = FoodSpecification::new;
		SpecificationBuilder<Food> specificationBuilder = SpecificationBuilder.of(filter, foodFactory, dependencies);
		Specification<Food> specification = specificationBuilder.build();

		Page<Food> foodPage = foodRepository.findAll(specification, pageable);

		List<FoodSummaryDto> summaries = foodPage.getContent().stream().map(foodMapper::toSummaryDto).toList();

		foodPopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, foodPage.getTotalElements());
	}

	@Override
	public List<Food> getAllFoodEntities() {
		return foodRepository.findAll();
	}

	private Food find(int foodId) {
		return repositoryHelper.find(foodRepository, Food.class, foodId);
	}

	private FoodResponseDto findAndMap(int foodId) {
		Food food = foodRepository.findByIdWithMedia(foodId)
			.orElseThrow(() -> RecordNotFoundException.of(Food.class, foodId));
		FoodResponseDto dto = foodMapper.toDetailedResponseDto(food);
		foodPopulationService.populate(dto);
		return dto;
	}

	private FoodUpdateDto applyPatchToFood(JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, FoodUpdateDto.class);
	}

	private FoodCategory findCategory(Integer categoryId) {
		if (categoryId == null) {
			return null;
		}
		return foodCategoryRepository.findById(categoryId)
			.orElseThrow(() -> RecordNotFoundException.of(FoodCategory.class, categoryId));
	}

}
