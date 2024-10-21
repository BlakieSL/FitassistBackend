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
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Food.Food;
import source.code.repository.FoodRepository;
import source.code.service.declaration.Food.FoodService;

import java.util.List;
import java.util.stream.Collectors;

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

  @Transactional
  public FoodResponseDto createFood(FoodCreateDto request) {
    Food food = foodRepository.save(foodMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new FoodCreateEvent(this, food));

    return foodMapper.toResponseDto(food);
  }

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

  public void deleteFood(int foodId) {
    Food food = find(foodId);
    foodRepository.delete(food);

    applicationEventPublisher.publishEvent(new FoodDeleteEvent(this, food));
  }

  public FoodCalculatedMacrosResponseDto calculateFoodMacros(int id,
                                                             CalculateFoodMacrosRequestDto request) {
    Food food = find(id);
    double factor = (double) request.getAmount() / 100;

    return foodMapper.toDtoWithFactor(food, factor);
  }

  public List<FoodResponseDto> searchFoods(SearchRequestDto request) {
    return foodRepository.findAllByNameContainingIgnoreCase(request.getName()).stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"foods"}, key = "#id")
  public FoodResponseDto getFood(int id) {
    Food food = find(id);
    return foodMapper.toResponseDto(food);
  }

  @Cacheable(value = {"allFoods"})
  public List<FoodResponseDto> getAllFoods() {
    return repositoryHelper.findAll(foodRepository, foodMapper::toResponseDto);
  }

  @Cacheable(value = {"foodsByCategory"}, key = "#categoryId")
  public List<FoodResponseDto> getFoodsByCategory(int categoryId) {
    return foodRepository.findAllByFoodCategory_Id(categoryId).stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
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
