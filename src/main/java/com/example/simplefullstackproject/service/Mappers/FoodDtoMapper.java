package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.FoodCategoryDto;
import com.example.simplefullstackproject.dto.FoodDto;
import com.example.simplefullstackproject.model.FoodCategory;
import com.example.simplefullstackproject.model.Food;
import com.example.simplefullstackproject.repository.FoodCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FoodDtoMapper {
    private final FoodCategoryRepository foodCategoryRepository;

    public FoodDtoMapper(FoodCategoryRepository foodCategoryRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
    }

    public FoodDto map(Food food) {
        return new FoodDto(
                food.getId(),
                food.getName(),
                food.getCalories(),
                food.getProtein(),
                food.getFat(),
                food.getCarbohydrates(),
                food.getFoodCategory().getName()
        );
    }

    public Food map(FoodDto request) {
        Food food = new Food();
        food.setName(request.getName());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setFat(request.getFat());
        food.setCarbohydrates(request.getCarbohydrates());
        FoodCategory foodCategory = foodCategoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        food.setFoodCategory(foodCategory);
        return food;
    }

    public FoodCategoryDto map(FoodCategory request){
        return new FoodCategoryDto(request.getId(), request.getName());
    }
}
