package source.code.service.implementation.Food;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Food.FoodCreateEvent;
import source.code.dto.request.CalculateFoodMacrosRequestDto;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;
import source.code.dto.response.FoodResponseDto;
import source.code.mapper.FoodMapper;
import source.code.model.Food.Food;
import source.code.model.Food.FoodCategory;
import source.code.model.User.UserFood;
import source.code.repository.FoodCategoryRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.service.declaration.FoodService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private final FoodRepository foodRepository;
  private final FoodMapper foodMapper;
  private final FoodCategoryRepository foodCategoryRepository;
  private final UserFoodRepository userFoodRepository;

  public FoodServiceImpl(
          ApplicationEventPublisher applicationEventPublisher,
          FoodRepository foodRepository,
          FoodMapper foodMapper,
          FoodCategoryRepository foodCategoryRepository,
          UserFoodRepository userFoodRepository) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.foodRepository = foodRepository;
    this.foodMapper = foodMapper;
    this.foodCategoryRepository = foodCategoryRepository;
    this.userFoodRepository = userFoodRepository;
  }

  @Transactional
  public FoodResponseDto createFood(FoodCreateDto request) {
    Food food = foodRepository.save(foodMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new FoodCreateEvent(this, request));

    return foodMapper.toDto(food);
  }

  public FoodCalculatedMacrosResponseDto calculateFoodMacros(
          int id,
          CalculateFoodMacrosRequestDto request) {

    Food food = foodRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + id + " not found"));
    double factor = (double) request.getAmount() / 100;

    return foodMapper.toDtoWithFactor(food, factor);
  }

  public List<FoodResponseDto> searchFoods(SearchRequestDto request) {
    List<Food> foods = foodRepository.findAllByNameContainingIgnoreCase(request.getName());

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"foods"}, key = "#id")
  public FoodResponseDto getFood(int id) {
    Food food = foodRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + id + " not found"));

    return foodMapper.toDto(food);
  }

  @Cacheable(value = {"allFoods"})
  public List<FoodResponseDto> getAllFoods() {
    List<Food> foods = foodRepository.findAll();

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<FoodResponseDto> getFoodsByUserAndType(int userId, short type) {
    List<UserFood> userFoods = userFoodRepository.findByUserIdAndType(userId, type);

    List<Food> foods = userFoods.stream()
            .map(UserFood::getFood)
            .collect(Collectors.toList());

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"allFoodCategories"})
  public List<FoodCategoryResponseDto> getAllCategories() {
    List<FoodCategory> categories = foodCategoryRepository.findAll();

    return categories.stream()
            .map(foodMapper::toCategoryDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"foodsByCategory"}, key = "#categoryId")
  public List<FoodResponseDto> getFoodsByCategory(int categoryId) {
    List<Food> foods = foodRepository
            .findAllByFoodCategory_Id(categoryId);

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }
}
