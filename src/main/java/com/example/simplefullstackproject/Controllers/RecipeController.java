package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.RecipeDto;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import com.example.simplefullstackproject.Models.Recipe;
import com.example.simplefullstackproject.Services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
            @Valid @RequestBody RecipeDto recipeDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        RecipeDto response = recipeService.save(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}/user")
    public ResponseEntity<List<RecipeDto>> getRecipesByUserId(@PathVariable Integer userId) {
        List<RecipeDto> recipes = recipeService.getRecipesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }


}
