package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.RecipeCategoryDto;
import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recipe-categories")
public class RecipeCategoryController {
    private final RecipeService recipeService;
    public RecipeCategoryController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeCategoryDto>> getAllRecipeCategories() {
        return ResponseEntity.ok(recipeService.getCategories());
    }

    @GetMapping("/{categoryId}/recipes")
    public ResponseEntity<List<RecipeDto>> getRecipesByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(categoryId));
    }
}
