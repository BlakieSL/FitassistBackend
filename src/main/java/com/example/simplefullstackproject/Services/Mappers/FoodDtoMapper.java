package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.FoodDto;
import com.example.simplefullstackproject.Models.Category;
import com.example.simplefullstackproject.Models.Food;
import com.example.simplefullstackproject.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FoodDtoMapper {
    private final CategoryRepository categoryRepository;
    public FoodDtoMapper(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }
    public FoodDto map(Food food){
        return new FoodDto(
                food.getId(),
                food.getName(),
                food.getCalories(),
                food.getProtein(),
                food.getFat(),
                food.getCarbohydrates(),
                food.getCategory().getName()
        );
    }
    public Food map(FoodDto request){
        Food food = new Food();
        food.setName(request.getName());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setFat(request.getFat());
        food.setCarbohydrates(request.getCarbohydrates());
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        food.setCategory(category);
        return food;
    }
}
