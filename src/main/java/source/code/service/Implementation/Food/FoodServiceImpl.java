package source.code.service.Implementation.Food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Filter.FilterDto;
import source.code.event.events.Food.FoodCreateEvent;
import source.code.event.events.Food.FoodDeleteEvent;
import source.code.event.events.Food.FoodUpdateEvent;
import source.code.dto.Request.Food.CalculateFoodMacrosRequestDto;
import source.code.dto.Request.Food.FoodCreateDto;
import source.code.dto.Request.Food.FoodUpdateDto;
import source.code.dto.Response.FoodCalculatedMacrosResponseDto;
import source.code.dto.Response.FoodResponseDto;
import source.code.helper.Enum.CacheNames;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Activity.Activity;
import source.code.model.Food.Food;
import source.code.repository.FoodRepository;
import source.code.service.Declaration.Food.FoodService;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ActivitySpecification;
import source.code.specification.specification.FoodSpecification;

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
    applicationEventPublisher.publishEvent(new FoodCreateEvent(this, food));

    return foodMapper.toResponseDto(food);
  }

  @Override
  @Transactional
  public void updateFood(int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    Food food = find(foodId);
    FoodUpdateDto patchedFoodUpdateDto = applyPatchToFood(food, patch);

    validationService.validate(patchedFoodUpdateDto);

    foodMapper.updateFood(food, patchedFoodUpdateDto);
    Food savedFood = foodRepository.save(food);

    applicationEventPublisher.publishEvent(new FoodUpdateEvent(this, savedFood));
  }

  @Override
  public void deleteFood(int foodId) {
    Food food = find(foodId);
    foodRepository.delete(food);

    applicationEventPublisher.publishEvent(new FoodDeleteEvent(this, food));
  }

  @Override
  public FoodCalculatedMacrosResponseDto calculateFoodMacros(int id,
                                                             CalculateFoodMacrosRequestDto request) {
    Food food = find(id);
    double factor = (double) request.getAmount() / 100;

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
    SpecificationFactory<Food> foodFactory = FoodSpecification::new;
    SpecificationBuilder<Food> specificationBuilder =
            new SpecificationBuilder<>(filter, foodFactory);
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
          throws JsonPatchException, JsonProcessingException {

    FoodResponseDto responseDto = foodMapper.toResponseDto(food);
    return jsonPatchService.applyPatch(patch, responseDto, FoodUpdateDto.class);
  }
}
