package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.CalculateAmountRequest;
import com.example.simplefullstackproject.Dtos.FoodDto;
import com.example.simplefullstackproject.Models.Food;
import com.example.simplefullstackproject.Repositories.FoodRepository;
import com.example.simplefullstackproject.Services.Mappers.FoodDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodDtoMapper foodDtoMapper;
    private final ValidationHelper validationHelper;
    public FoodService(FoodRepository foodRepository,
                       FoodDtoMapper foodDtoMapper,
                       ValidationHelper validationHelper){
        this.foodRepository = foodRepository;
        this.foodDtoMapper = foodDtoMapper;
        this.validationHelper = validationHelper;
    }

    @Transactional
    public FoodDto saveFood(FoodDto request){
        validationHelper.validate(request);

        Food food = foodRepository.save(foodDtoMapper.map(request));
        return foodDtoMapper.map(food);
    }

    public FoodDto getFoodById(Integer id){
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + id + " not found"));
        return foodDtoMapper.map(food);
    }

    public FoodDto calculateMacros(int id, CalculateAmountRequest request){
        validationHelper.validate(request);
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + id + " not found"));

        double factor = (double) request.getAmount() / 100;

        food.setCalories(food.getCalories()*factor);
        food.setProtein(food.getProtein()*factor);
        food.setFat(food.getFat()*factor);
        food.setCarbohydrates(food.getCarbohydrates()*factor);

        return foodDtoMapper.map(food);
    }
}
