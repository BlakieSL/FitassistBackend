package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.RecipeDto;
import com.example.simplefullstackproject.Models.Recipe;
import com.example.simplefullstackproject.Services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Integer id){
        try{
            RecipeDto recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createRecipe(
            @Valid @RequestBody RecipeDto recipeDto,
            BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }

            RecipeDto response = recipeService.save(recipeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
