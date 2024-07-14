package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.CalculateAmountRequest;
import com.example.simplefullstackproject.Dtos.FoodDto;
import com.example.simplefullstackproject.Dtos.SearchDtoRequest;
import com.example.simplefullstackproject.Services.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<?> getAllFoods() {
        return ResponseEntity.ok(foodService.getFoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable int id) {
        FoodDto food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    @PostMapping("/{id}/calculate-macros")
    public ResponseEntity<?> calculateFoodMacrosById(
            @PathVariable int id,
            @Valid @RequestBody CalculateAmountRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        FoodDto response = foodService.calculateMacros(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createFood(
            @Valid @RequestBody FoodDto foodDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        FoodDto response = foodService.saveFood(foodDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchFood(
            @Valid @RequestBody SearchDtoRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(foodService.searchFoods(request));
    }
}
