package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.AddFoodRecipeDto;
import com.example.simplefullstackproject.Services.RecipeFoodService;
import jakarta.validation.Valid;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe-food")
public class RecipeFoodController {
    private final RecipeFoodService recipeFoodService;

    public RecipeFoodController(RecipeFoodService recipeFoodService) {
        this.recipeFoodService = recipeFoodService;
    }

    @PostMapping("/{recipeId}/add")
    public ResponseEntity<?> addFoodToRecipe(
            @PathVariable Integer recipeId,
            @Valid @RequestBody AddFoodRecipeDto request,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        recipeFoodService.addFoodToRecipe(recipeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{recipeId}/remove/{foodId}")
    public ResponseEntity<?> deleteFoodFromRecipe(
            @PathVariable Integer recipeId, @PathVariable Integer foodId) {
        recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{recipeId}/modify")
    public ResponseEntity<?> modifyFoodRecipe(
            @PathVariable Integer recipeId,
            @Valid @RequestBody AddFoodRecipeDto request,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        recipeFoodService.modifyFoodRecipe(recipeId, request);
        return ResponseEntity.ok().build();
    }
}