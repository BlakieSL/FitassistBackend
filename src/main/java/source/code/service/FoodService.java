package source.code.service;

import source.code.dto.*;
import source.code.helper.ValidationHelper;
import source.code.mapper.FoodMapper;
import source.code.model.Food;
import source.code.model.FoodCategory;
import source.code.model.UserFood;
import source.code.repository.FoodCategoryRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;
    private final ValidationHelper validationHelper;

    private final FoodCategoryRepository foodCategoryRepository;
    private final UserFoodRepository userFoodRepository;
    public FoodService(
            FoodRepository foodRepository,
            FoodMapper foodMapper,
            ValidationHelper validationHelper,
            FoodCategoryRepository foodCategoryRepository,
            UserFoodRepository userFoodRepository) {
        this.foodRepository = foodRepository;
        this.foodMapper = foodMapper;
        this.validationHelper = validationHelper;
        this.foodCategoryRepository = foodCategoryRepository;
        this.userFoodRepository = userFoodRepository;
    }

    @Transactional
    public FoodDto saveFood(FoodAdditionDto request) {
        validationHelper.validate(request);
        Food food = foodRepository.save(foodMapper.toEntity(request));
        return foodMapper.toDto(food);
    }

    public FoodDto getFoodById(int id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Food with id: " + id + " not found"));
        return foodMapper.toDto(food);
    }

    public List<FoodDto> getFoods() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public FoodCalculatedDto calculateMacros(int id, CalculateAmountRequest request) {
        validationHelper.validate(request);
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + id + " not found"));
        double factor = (double) request.getAmount() / 100;
        return foodMapper.toDtoWithFactor(food, factor);
    }

    public List<FoodDto> searchFoods(SearchDtoRequest request) {
        List<Food> foods = foodRepository.findAllByNameContainingIgnoreCase(request.getName());
        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getFoodsByUserID(int userId) {
        List<UserFood> userFoods = userFoodRepository.findByUserId(userId);
        List<Food> foods = userFoods.stream()
                .map(UserFood::getFood)
                .collect(Collectors.toList());
        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FoodCategoryDto> getCategories() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        return categories.stream()
                .map(foodMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getFoodsByCategory(int categoryId){
        List<Food> foods = foodRepository
                .findAllByFoodCategory_Id(categoryId);
        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }
}
