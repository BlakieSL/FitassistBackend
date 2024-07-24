package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Components.JsonPatchHelper;
import com.example.simplefullstackproject.Dtos.DailyCartFoodDto;
import com.example.simplefullstackproject.Dtos.FoodDtoResponse;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import com.example.simplefullstackproject.Services.DailyCartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyCartController {
    private final DailyCartService dailyCartService;
    public DailyCartController(DailyCartService dailyCartService) {
        this.dailyCartService = dailyCartService;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<FoodDtoResponse>> getAllFoodsInCartByUserID(@PathVariable int userId) {
        List<FoodDtoResponse> foods = dailyCartService.getFoodsInCart(userId);
        return ResponseEntity.ok(foods);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addFoodToCartByUserId(
            @PathVariable int userId,
            @Valid @RequestBody DailyCartFoodDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        dailyCartService.addFoodToCart(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{foodId}")
    public ResponseEntity<Void> removeFoodFromCart(@PathVariable int userId, @PathVariable int foodId) {
        dailyCartService.removeFoodFromCart(userId, foodId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-food/{foodId}")
    public ResponseEntity<Void> modifyDailyCartFood(
            @PathVariable Integer userId,
            @PathVariable Integer foodId,
            @RequestBody JsonMergePatch patch,
            BindingResult bindingResult
            ) throws JsonPatchException, JsonProcessingException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }

        dailyCartService.modifyDailyCartFood(userId, foodId, patch);
        return ResponseEntity.noContent().build();
    }
}
