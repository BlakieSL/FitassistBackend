package source.code.service.implementation.Food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Food.FoodCreateEvent;
import source.code.cache.event.Food.FoodDeleteEvent;
import source.code.cache.event.Food.FoodUpdateEvent;
import source.code.dto.request.Food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.Food.FoodCreateDto;
import source.code.dto.request.Food.FoodUpdateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodResponseDto;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Food.Food;
import source.code.repository.FoodCategoryRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.service.declaration.Food.FoodService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ValidationServiceImpl validationServiceImpl;
  private final JsonPatchServiceImpl jsonPatchServiceImpl;
  private final FoodRepository foodRepository;
  private final FoodMapper foodMapper;
  private final FoodCategoryRepository foodCategoryRepository;
  private final UserFoodRepository userFoodRepository;

  public FoodServiceImpl(
          ApplicationEventPublisher applicationEventPublisher,
          ValidationServiceImpl validationServiceImpl,
          JsonPatchServiceImpl jsonPatchServiceImpl,
          FoodRepository foodRepository,
          FoodMapper foodMapper,
          FoodCategoryRepository foodCategoryRepository,
          UserFoodRepository userFoodRepository) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.validationServiceImpl = validationServiceImpl;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
    this.foodRepository = foodRepository;
    this.foodMapper = foodMapper;
    this.foodCategoryRepository = foodCategoryRepository;
    this.userFoodRepository = userFoodRepository;
  }

  @Transactional
  public FoodResponseDto createFood(FoodCreateDto request) {
    Food food = foodRepository.save(foodMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new FoodCreateEvent(this, food));

    return foodMapper.toResponseDto(food);
  }

  @Transactional
  public void updateFood(int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    Food food = getFoodOrThrow(foodId);
    FoodUpdateDto patchedFoodUpdateDto = applyPatchToFood(food, patch);

    validationServiceImpl.validate(patchedFoodUpdateDto);

    foodMapper.updateFood(food, patchedFoodUpdateDto);
    Food savedFood = foodRepository.save(food);

    applicationEventPublisher.publishEvent(new FoodUpdateEvent(this, savedFood));
  }

  public void deleteFood(int foodId) {
    Food food = getFoodOrThrow(foodId);
    foodRepository.delete(food);

    applicationEventPublisher.publishEvent(new FoodDeleteEvent(this, food));
  }

  public FoodCalculatedMacrosResponseDto calculateFoodMacros(int id,
                                                             CalculateFoodMacrosRequestDto request) {
    Food food = getFoodOrThrow(id);
    double factor = (double) request.getAmount() / 100;

    return foodMapper.toDtoWithFactor(food, factor);
  }

  public List<FoodResponseDto> searchFoods(SearchRequestDto request) {
    List<Food> foods = foodRepository.findAllByNameContainingIgnoreCase(request.getName());

    return foods.stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"foods"}, key = "#id")
  public FoodResponseDto getFood(int id) {
    Food food = getFoodOrThrow(id);
    return foodMapper.toResponseDto(food);
  }

  @Cacheable(value = {"allFoods"})
  public List<FoodResponseDto> getAllFoods() {
    List<Food> foods = foodRepository.findAll();

    return foods.stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"foodsByCategory"}, key = "#categoryId")
  public List<FoodResponseDto> getFoodsByCategory(int categoryId) {
    List<Food> foods = foodRepository
            .findAllByFoodCategory_Id(categoryId);

    return foods.stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  private Food getFoodOrThrow(int foodId) {
    return foodRepository.findById(foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + foodId + " not found"));
  }

  private FoodUpdateDto applyPatchToFood(Food food, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    FoodResponseDto responseDto = foodMapper.toResponseDto(food);
    return jsonPatchServiceImpl.applyPatch(patch, responseDto, FoodUpdateDto.class);
  }
}
