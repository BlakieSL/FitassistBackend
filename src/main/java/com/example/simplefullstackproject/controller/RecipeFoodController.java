package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.AddFoodRecipeDto;
import com.example.simplefullstackproject.service.RecipeFoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import com.example.simplefullstackproject.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe-food")
public class RecipeFoodController {
    private final RecipeFoodService recipeFoodService;
    private final ObjectMapper objectMapper;

    public RecipeFoodController(RecipeFoodService recipeFoodService,
                                ObjectMapper objectMapper) {
        this.recipeFoodService = recipeFoodService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{recipeId}/add/{foodId}")
    public ResponseEntity<Void> addFoodToRecipe(
            @PathVariable int recipeId,
            @PathVariable int foodId,
            @Valid @RequestBody AddFoodRecipeDto request,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        recipeFoodService.addFoodToRecipe(recipeId, foodId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{recipeId}/remove/{foodId}")
    public ResponseEntity<Void> deleteFoodFromRecipe(
            @PathVariable int recipeId, @PathVariable int foodId) {
        recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{recipeId}/modify/{foodId}")
    public ResponseEntity<Void> modifyFoodRecipe(
            @PathVariable int recipeId,
            @PathVariable int foodId,
            @Valid @RequestBody JsonMergePatch patch,
            BindingResult bindingResult) throws JsonPatchException, JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        recipeFoodService.modifyFoodRecipe(recipeId, foodId, patch);
        return ResponseEntity.ok().build();
    }
}
