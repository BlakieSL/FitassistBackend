package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.CalculateAmountRequest;
import com.example.simplefullstackproject.dto.FoodCategoryDto;
import com.example.simplefullstackproject.dto.FoodDto;
import com.example.simplefullstackproject.dto.SearchDtoRequest;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.FoodMapper;
import com.example.simplefullstackproject.model.Food;
import com.example.simplefullstackproject.model.FoodCategory;
import com.example.simplefullstackproject.model.UserFood;
import com.example.simplefullstackproject.repository.FoodCategoryRepository;
import com.example.simplefullstackproject.repository.FoodRepository;
import com.example.simplefullstackproject.repository.UserFoodRepository;
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
    public FoodDto saveFood(FoodDto request) {
        validationHelper.validate(request);
        Food food = foodRepository.save(foodMapper.toEntity(request));
        return foodMapper.toDto(food);
    }

    public FoodDto getFoodById(Integer id) {
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

    public FoodDto calculateMacros(int id, CalculateAmountRequest request) {
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

    public List<FoodDto> getFoodsByUserID(Integer userId) {
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

    public List<FoodDto> getFoodsByCategory(Integer categoryId){
        List<Food> foods = foodRepository
                .findAllByFoodCategory_Id(categoryId);
        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }
}
