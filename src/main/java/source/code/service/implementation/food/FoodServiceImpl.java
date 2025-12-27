package source.code.service.implementation.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.request.food.FoodUpdateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.event.events.Food.FoodCreateEvent;
import source.code.event.events.Food.FoodDeleteEvent;
import source.code.event.events.Food.FoodUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.FoodMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.food.Food;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.food.FoodPopulationService;
import source.code.service.declaration.food.FoodService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.FoodSpecification;

@Service
public class FoodServiceImpl implements FoodService {

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final FoodMapper foodMapper;

	private final RecipeMapper recipeMapper;

	private final RepositoryHelper repositoryHelper;

	private final FoodRepository foodRepository;

	private final RecipeRepository recipeRepository;

	private final FoodPopulationService foodPopulationService;

	private final RecipePopulationService recipePopulationService;

	private final SpecificationDependencies dependencies;

	public FoodServiceImpl(ApplicationEventPublisher applicationEventPublisher, ValidationService validationService,
						   JsonPatchService jsonPatchService, FoodRepository foodRepository, RecipeRepository recipeRepository,
						   FoodMapper foodMapper, RecipeMapper recipeMapper, RepositoryHelper repositoryHelper,
						   FoodPopulationService foodPopulationService, RecipePopulationService recipePopulationService,
						   SpecificationDependencies dependencies) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.foodRepository = foodRepository;
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
		Food saved = foodRepository.save(foodMapper.toEntity(request));

		foodRepository.flush();

		Food food = foodRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Food.class, saved.getId()));

		applicationEventPublisher.publishEvent(FoodCreateEvent.of(this, food));

		FoodResponseDto dto = foodMapper.toDetailedResponseDto(food);
		foodPopulationService.populate(dto);
		return dto;
	}

	@Override
	@Transactional
	public void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Food food = find(foodId);
		FoodUpdateDto patchedFoodUpdateDto = applyPatchToFood(patch);

		validationService.validate(patchedFoodUpdateDto);
		foodMapper.updateFood(food, patchedFoodUpdateDto);
		Food saved = foodRepository.save(food);

		foodRepository.flush();

		Food refetchedFood = foodRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Food.class, saved.getId()));

		applicationEventPublisher.publishEvent(FoodUpdateEvent.of(this, refetchedFood));
	}

	@Override
	public void deleteFood(int foodId) {
		Food food = find(foodId);
		foodRepository.delete(food);

		applicationEventPublisher.publishEvent(FoodDeleteEvent.of(this, food));
	}

	@Override
	public FoodCalculatedMacrosResponseDto calculateFoodMacros(int id, CalculateFoodMacrosRequestDto request) {
		Food food = find(id);
		BigDecimal quantity = request.getQuantity();
		BigDecimal divisor = new BigDecimal("100");

		BigDecimal factor = quantity.divide(divisor, 10, RoundingMode.HALF_UP);

		return foodMapper.toDtoWithFactor(food, factor);
	}

	@Override
	@Cacheable(value = CacheNames.FOODS, key = "#id")
	public FoodResponseDto getFood(int id) {
		FoodResponseDto dto = findAndMap(id);

		var summaries = recipeRepository.findAllWithDetailsByFoodId(id)
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

}
