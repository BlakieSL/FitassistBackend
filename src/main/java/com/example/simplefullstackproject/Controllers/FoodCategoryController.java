package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.ActivityCategoryDto;
import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.FoodCategoryDto;
import com.example.simplefullstackproject.Dtos.FoodDto;
import com.example.simplefullstackproject.Models.ActivityCategory;
import com.example.simplefullstackproject.Services.ActivityService;
import com.example.simplefullstackproject.Services.FoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/food-categories")
public class FoodCategoryController {
    private final FoodService foodService;

    public FoodCategoryController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodCategoryDto>> getAllFoodCategories() {
        return ResponseEntity.ok(foodService.getCategories());
    }

    @GetMapping("/{categoryId}/foods")
    public ResponseEntity<List<FoodDto>> getFoodsByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(foodService.getFoodsByCategory(categoryId));
    }
}