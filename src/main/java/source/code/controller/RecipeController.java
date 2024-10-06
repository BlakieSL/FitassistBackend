package source.code.controller;

import source.code.dto.LikesAndSavedDto;
import source.code.dto.RecipeAdditionDto;
import source.code.dto.RecipeDto;
import source.code.exception.ValidationException;
import source.code.service.RecipeService;
import source.code.service.UserRecipeService;
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
    private final UserRecipeService userRecipeService;
    public RecipeController(RecipeService recipeService, UserRecipeService userRecipeService) {
        this.recipeService = recipeService;
        this.userRecipeService = userRecipeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable int id) {
        RecipeDto recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getRecipes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDto>> getRecipesByUserId(@PathVariable int userId) {
        List<RecipeDto> recipes = recipeService.getRecipesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavedDto> getLikesAndSavesRecipe(@PathVariable int id) {
        LikesAndSavedDto dto = userRecipeService.calculateLikesAndSavesByRecipeId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody RecipeAdditionDto recipeDto) {
        RecipeDto response = recipeService.save(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
