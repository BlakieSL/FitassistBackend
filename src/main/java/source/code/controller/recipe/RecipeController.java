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
    public ResponseEntity<RecipeResponseDto> createRecipe(
            @Valid @RequestBody RecipeCreateDto recipeDto
    ) {
        RecipeResponseDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RecipeOwnerOrAdmin
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        recipeService.updateRecipe(id, patch);
        return ResponseEntity.noContent().build();
    }

    @RecipeOwnerOrAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PublicRecipeOrOwnerOrAdmin
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> getRecipe(@PathVariable int id) {
        RecipeResponseDto recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping({"/private", "/private/{isPrivate}"})
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipes(@PathVariable(required = false) Boolean isPrivate) {
        return ResponseEntity.ok(recipeService.getAllRecipes(isPrivate));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RecipeResponseDto>> getFilteredRecipes(
            @Valid @RequestBody FilterDto filterDto
    ) {
        List<RecipeResponseDto> filtered = recipeService.getFilteredRecipes(filterDto);
        return ResponseEntity.ok(filtered);
    }
}
