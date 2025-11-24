package source.code.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.recipe.PublicRecipeOrOwnerOrAdmin;
import source.code.annotation.recipe.RecipeOwnerOrAdmin;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.declaration.recipe.RecipeService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeSummaryDto> createRecipe(
            @Valid @RequestBody RecipeCreateDto recipeDto
    ) {
        RecipeSummaryDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RecipeOwnerOrAdmin
    @PatchMapping("/{recipeId}")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable int recipeId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        recipeService.updateRecipe(recipeId, patch);
        return ResponseEntity.noContent().build();
    }

    @RecipeOwnerOrAdmin
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    @PublicRecipeOrOwnerOrAdmin
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDto> getRecipe(@PathVariable int recipeId) {
        RecipeResponseDto recipe = recipeService.getRecipe(recipeId);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping({"/private", "/private/{isPrivate}"})
    public ResponseEntity<List<RecipeSummaryDto>> getAllRecipes(@PathVariable(required = false) Boolean isPrivate) {
        return ResponseEntity.ok(recipeService.getAllRecipes(isPrivate));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RecipeSummaryDto>> getFilteredRecipes(
            @Valid @RequestBody FilterDto filterDto
    ) {
        List<RecipeSummaryDto> filtered = recipeService.getFilteredRecipes(filterDto);
        return ResponseEntity.ok(filtered);
    }

    @PatchMapping("/{recipeId}/view")
    public ResponseEntity<Void> incrementViews(@PathVariable int recipeId) {
        recipeService.incrementViews(recipeId);
        return ResponseEntity.noContent().build();
    }
}
