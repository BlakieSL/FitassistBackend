package source.code.service.implementation.Food;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
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
  private final FoodRepository foodRepository;
  private final FoodMapper foodMapper;
  private final FoodCategoryRepository foodCategoryRepository;
  private final UserFoodRepository userFoodRepository;

  public FoodServiceImpl(
          FoodRepository foodRepository,
          FoodMapper foodMapper,
          FoodCategoryRepository foodCategoryRepository,
          UserFoodRepository userFoodRepository) {
    this.foodRepository = foodRepository;
    this.foodMapper = foodMapper;
    this.foodCategoryRepository = foodCategoryRepository;
    this.userFoodRepository = userFoodRepository;
  }

  @Transactional
  public FoodResponseDto createFood(FoodCreateDto request) {
    Food food = foodRepository.save(foodMapper.toEntity(request));

    return foodMapper.toDto(food);
  }

  public FoodResponseDto getFood(int id) {
    Food food = foodRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + id + " not found"));

    return foodMapper.toDto(food);
  }

  public List<FoodResponseDto> getAllFoods() {
    List<Food> foods = foodRepository.findAll();

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
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

  public List<FoodResponseDto> getFoodsByUser(int userId) {
    List<UserFood> userFoods = userFoodRepository.findByUserId(userId);
    List<Food> foods = userFoods.stream()
            .map(UserFood::getFood)
            .collect(Collectors.toList());

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<FoodCategoryResponseDto> getAllCategories() {
    List<FoodCategory> categories = foodCategoryRepository.findAll();

    return categories.stream()
            .map(foodMapper::toCategoryDto)
            .collect(Collectors.toList());
  }

  public List<FoodResponseDto> getFoodsByCategory(int categoryId) {
    List<Food> foods = foodRepository
            .findAllByFoodCategory_Id(categoryId);

    return foods.stream()
            .map(foodMapper::toDto)
            .collect(Collectors.toList());
  }
}
