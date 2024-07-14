package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.AddFoodRecipeDto;
import com.example.simplefullstackproject.Services.RecipeFoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
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
    private final ObjectMapper objectMapper;

    public RecipeFoodController(RecipeFoodService recipeFoodService,
                                ObjectMapper objectMapper) {
        this.recipeFoodService = recipeFoodService;
        this.objectMapper = objectMapper;
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

    @PatchMapping("/{recipeId}/modify/foodId")
    public ResponseEntity<?> modifyFoodRecipe(
            @PathVariable Integer recipeId,
            @PathVariable Integer foodId,
            @Valid @RequestBody JsonMergePatch patch,
            BindingResult bindingResult) throws JsonPatchException, JsonProcessingException {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AddFoodRecipeDto existingRecipeFood = recipeFoodService.getRecipeFoodByRecipeIdAndFoodId(recipeId, foodId);
        AddFoodRecipeDto request = applyPatch(existingRecipeFood, patch);

        recipeFoodService.modifyFoodRecipe(recipeId, request);
        return ResponseEntity.ok().build();
    }

    private AddFoodRecipeDto applyPatch(AddFoodRecipeDto existingRecipeFood, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode recipeFoodNode = objectMapper.valueToTree(existingRecipeFood);
        JsonNode patchedNode = patch.apply(recipeFoodNode);
        return objectMapper.treeToValue(patchedNode, AddFoodRecipeDto.class);
    }
}
