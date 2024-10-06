package source.code.controller;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.RecipeResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.service.interfaces.RecipeService;
import source.code.service.interfaces.UserRecipeService;

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
    public ResponseEntity<RecipeResponseDto> getRecipe(@PathVariable int id) {
        RecipeResponseDto recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByUser(@PathVariable int userId) {
        List<RecipeResponseDto> recipes = recipeService.getRecipesByUser(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> getRecipeLikesAndSaves(@PathVariable int id) {
        LikesAndSavesResponseDto dto = userRecipeService.calculateRecipeLikesAndSaves(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<RecipeResponseDto> createRecipe(@Valid @RequestBody RecipeCreateDto recipeDto) {
        RecipeResponseDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
