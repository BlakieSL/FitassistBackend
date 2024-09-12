package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodDto>> getAllFoods() {
        return ResponseEntity.ok(foodService.getFoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDto> getFoodById(@PathVariable int id) {
        FoodDto food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    @PostMapping("/{id}/calculate-macros")
    public ResponseEntity<FoodCalculatedDto> calculateFoodMacrosById(
            @PathVariable int id,
            @Valid @RequestBody CalculateAmountRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        FoodCalculatedDto response = foodService.calculateMacros(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FoodDto> createFood(
            @Valid @RequestBody FoodAdditionDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        FoodDto response = foodService.saveFood(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<FoodDto>> searchFoods(
            @Valid @RequestBody SearchDtoRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(foodService.searchFoods(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodDto>> getFoodsByUserId(@PathVariable Integer userId) {
        List<FoodDto> recipes = foodService.getFoodsByUserID(userId);
        return ResponseEntity.ok(recipes);
    }
}
