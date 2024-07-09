package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.DailyCartFoodDto;
import com.example.simplefullstackproject.Dtos.FoodDtoResponse;
import com.example.simplefullstackproject.Services.DailyCartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;



@RestController
@RequestMapping(path = "/api")
public class DailyCartController {
    private final DailyCartService dailyCartService;
    public DailyCartController(DailyCartService dailyCartService){
        this.dailyCartService = dailyCartService;
    }
    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getFoodsInCart(@PathVariable int userId){
        try{
            List<FoodDtoResponse> foods = dailyCartService.getFoodsInCart(userId);
            return ResponseEntity.ok(foods);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/cart/{userId}/add")
    public ResponseEntity<?> addFoodToCart(@PathVariable int userId,
                                           @Valid @RequestBody DailyCartFoodDto request,
                                           BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }
            dailyCartService.addFoodToCart(userId,request);
            return ResponseEntity.ok().build();
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/{userId}/remove/{foodId}")
    public ResponseEntity<?> removeFoodFromCart(@PathVariable int userId,
                                                @PathVariable int foodId){
        try{
            dailyCartService.removeFoodFromCart(userId,foodId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
