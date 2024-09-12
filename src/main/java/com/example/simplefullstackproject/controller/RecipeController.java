package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.RecipeAdditionDto;
import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Integer id) {
        RecipeDto recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestBody RecipeAdditionDto recipeDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        RecipeDto response = recipeService.save(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDto>> getRecipesByUserId(@PathVariable Integer userId) {
        List<RecipeDto> recipes = recipeService.getRecipesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }
}
