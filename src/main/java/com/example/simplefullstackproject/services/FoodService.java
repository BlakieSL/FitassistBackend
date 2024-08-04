package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.CalculateAmountRequest;
import com.example.simplefullstackproject.dtos.FoodCategoryDto;
import com.example.simplefullstackproject.dtos.FoodDto;
import com.example.simplefullstackproject.dtos.SearchDtoRequest;
import com.example.simplefullstackproject.models.Food;
import com.example.simplefullstackproject.models.FoodCategory;
import com.example.simplefullstackproject.models.UserFood;
import com.example.simplefullstackproject.repositories.FoodCategoryRepository;
import com.example.simplefullstackproject.repositories.FoodRepository;
import com.example.simplefullstackproject.repositories.UserFoodRepository;
import com.example.simplefullstackproject.services.Mappers.FoodDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodDtoMapper foodDtoMapper;
    private final ValidationHelper validationHelper;

    private final FoodCategoryRepository foodCategoryRepository;
    private final UserFoodRepository userFoodRepository;
    public FoodService(
            FoodRepository foodRepository,
            FoodDtoMapper foodDtoMapper,
            ValidationHelper validationHelper,
            FoodCategoryRepository foodCategoryRepository,
            UserFoodRepository userFoodRepository) {
        this.foodRepository = foodRepository;
        this.foodDtoMapper = foodDtoMapper;
        this.validationHelper = validationHelper;
        this.foodCategoryRepository = foodCategoryRepository;
        this.userFoodRepository = userFoodRepository;
    }

    @Transactional
    public FoodDto saveFood(FoodDto request) {
        validationHelper.validate(request);

        Food food = foodRepository.save(foodDtoMapper.map(request));
        return foodDtoMapper.map(food);
    }

    public FoodDto getFoodById(Integer id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + id + " not found"));
        return foodDtoMapper.map(food);
    }

    public List<FoodDto> getFoods() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream()
                .map(foodDtoMapper::map)
                .collect(Collectors.toList());
    }

    public FoodDto calculateMacros(int id, CalculateAmountRequest request) {
        validationHelper.validate(request);

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + id + " not found"));

        double factor = (double) request.getAmount() / 100;

        food.setCalories(food.getCalories() * factor);
        food.setProtein(food.getProtein() * factor);
        food.setFat(food.getFat() * factor);
        food.setCarbohydrates(food.getCarbohydrates() * factor);

        return foodDtoMapper.map(food);
    }

    public List<FoodDto> searchFoods(SearchDtoRequest request) {
        List<Food> foods = foodRepository.findAllByNameContainingIgnoreCase(request.getName());
        return foods.stream()
                .map(foodDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getFoodsByUserID(Integer userId) {
        List<UserFood> userFoods = userFoodRepository.findByUserId(userId);
        List<Food> foods = userFoods.stream()
                .map(UserFood::getFood)
                .collect(Collectors.toList());
        return foods.stream()
                .map(foodDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<FoodCategoryDto> getCategories() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        return categories.stream()
                .map(foodDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getFoodsByCategory(Integer categoryId){
        List<Food> foods = foodRepository
                .findAllByFoodCategory_Id(categoryId);
        return foods.stream()
                .map(foodDtoMapper::map)
                .collect(Collectors.toList());
    }
}
