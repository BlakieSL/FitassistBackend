package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.DailyCartFoodDto;
import com.example.simplefullstackproject.dto.DailyCartResponse;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.DailyCartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyCartController {
    private final DailyCartService dailyCartService;
    public DailyCartController(DailyCartService dailyCartService) {
        this.dailyCartService = dailyCartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DailyCartResponse> getAllFoodsInCartByUserID(@PathVariable int userId) {
        DailyCartResponse cart = dailyCartService.getFoodsInCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add/{foodId}")
    public ResponseEntity<Void> addFoodToCartByUserId(
            @PathVariable int userId,
            @PathVariable int foodId,
            @Valid @RequestBody DailyCartFoodDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        dailyCartService.addFoodToCart(userId, foodId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{foodId}")
    public ResponseEntity<Void> removeFoodFromCart(@PathVariable int userId, @PathVariable int foodId) {
        dailyCartService.removeFoodFromCart(userId, foodId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-food/{foodId}")
    public ResponseEntity<Void> modifyDailyCartFood(
            @PathVariable int userId,
            @PathVariable int foodId,
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
