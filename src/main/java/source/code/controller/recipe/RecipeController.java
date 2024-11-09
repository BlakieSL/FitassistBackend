package source.code.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.service.declaration.recipe.RecipeService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
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

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByCategory(
            @PathVariable int categoryId
    ) {
        List<RecipeResponseDto> recipes = recipeService.getRecipesByCategory(categoryId);
        return ResponseEntity.ok(recipes);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RecipeResponseDto>> getFilteredRecipes(
            @Valid @RequestBody FilterDto filterDto
    ) {
        List<RecipeResponseDto> filtered = recipeService.getFilteredRecipes(filterDto);
        return ResponseEntity.ok(filtered);
    }

    @PostMapping
    public ResponseEntity<RecipeResponseDto> createRecipe(
            @Valid @RequestBody RecipeCreateDto recipeDto
    ) {
        RecipeResponseDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        recipeService.updateRecipe(id, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
