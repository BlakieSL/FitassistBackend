package source.code.service.implementation.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.request.food.FoodUpdateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.event.events.Food.FoodCreateEvent;
import source.code.event.events.Food.FoodDeleteEvent;
import source.code.event.events.Food.FoodUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.repository.FoodRepository;
import source.code.service.declaration.food.FoodService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.FoodSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FoodMapper foodMapper;
    private final RepositoryHelper repositoryHelper;
    private final FoodRepository foodRepository;

    public FoodServiceImpl(
            ApplicationEventPublisher applicationEventPublisher,
            ValidationService validationService,
            JsonPatchService jsonPatchService,
            FoodRepository foodRepository,
            FoodMapper foodMapper,
            RepositoryHelper repositoryHelper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.validationService = validationService;
        this.jsonPatchService = jsonPatchService;
        this.foodRepository = foodRepository;
        this.foodMapper = foodMapper;
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    @Transactional
    public FoodResponseDto createFood(FoodCreateDto request) {
        Food food = foodRepository.save(foodMapper.toEntity(request));
        applicationEventPublisher.publishEvent(FoodCreateEvent.of(this, food));

        return foodMapper.toResponseDto(food);
    }

    @Override
    @Transactional
    public void updateFood(int foodId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Food food = find(foodId);
        FoodUpdateDto patchedFoodUpdateDto = applyPatchToFood(food, patch);

        validationService.validate(patchedFoodUpdateDto);
        foodMapper.updateFood(food, patchedFoodUpdateDto);
        Food savedFood = foodRepository.save(food);

        applicationEventPublisher.publishEvent(FoodUpdateEvent.of(this, savedFood));
    }

    @Override
    public void deleteFood(int foodId) {
        Food food = find(foodId);
        foodRepository.delete(food);

        applicationEventPublisher.publishEvent(FoodDeleteEvent.of(this, food));
    }

    @Override
    public FoodCalculatedMacrosResponseDto calculateFoodMacros(
            int id, CalculateFoodMacrosRequestDto request
    ) {
        Food food = find(id);
        BigDecimal quantity = request.getQuantity();
        BigDecimal divisor = new BigDecimal("100");

        BigDecimal factor = quantity.divide(divisor, 10, RoundingMode.HALF_UP);

        return foodMapper.toDtoWithFactor(food, factor);
    }

    @Override
    @Cacheable(value = CacheNames.FOODS, key = "#id")
    public FoodResponseDto getFood(int id) {
        Food food = find(id);
        return foodMapper.toResponseDto(food);
    }

    @Override
    @Cacheable(value = CacheNames.ALL_FOODS)
    public List<FoodResponseDto> getAllFoods() {
        return repositoryHelper.findAll(foodRepository, foodMapper::toResponseDto);
    }

    @Override
    public List<FoodResponseDto> getFilteredFoods(FilterDto filter) {
        SpecificationFactory<Food> foodFactory = FoodSpecification::of;
        SpecificationBuilder<Food> specificationBuilder = SpecificationBuilder.of(filter, foodFactory);
        Specification<Food> specification = specificationBuilder.build();

        return foodRepository.findAll(specification).stream()
                .map(foodMapper::toResponseDto)
                .toList();
    }


    @Override
    public List<Food> getAllFoodEntities() {
        return foodRepository.findAllWithoutAssociations();
    }

    @Override
    @Cacheable(value = CacheNames.FOODS_BY_CATEGORY, key = "#categoryId")
    public List<FoodResponseDto> getFoodsByCategory(int categoryId) {
        return foodRepository.findAllByFoodCategory_Id(categoryId).stream()
                .map(foodMapper::toResponseDto)
                .toList();
    }

    private Food find(int foodId) {
        return repositoryHelper.find(foodRepository, Food.class, foodId);
    }

    private FoodUpdateDto applyPatchToFood(Food food, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        FoodResponseDto responseDto = foodMapper.toResponseDto(food);
        return jsonPatchService.applyPatch(patch, responseDto, FoodUpdateDto.class);
    }
}
