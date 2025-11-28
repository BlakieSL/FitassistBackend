package source.code.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping({"/private", "/private/{showPrivate}"})
    public ResponseEntity<Page<RecipeSummaryDto>> getAllRecipes(
            @PathVariable(required = false) Boolean showPrivate,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.getAllRecipes(showPrivate, pageable));
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<RecipeSummaryDto>> getFilteredRecipes(
            @Valid @RequestBody FilterDto filterDto,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RecipeSummaryDto> filtered = recipeService.getFilteredRecipes(filterDto, pageable);
        return ResponseEntity.ok(filtered);
    }

    @PatchMapping("/{recipeId}/view")
    public ResponseEntity<Void> incrementViews(@PathVariable int recipeId) {
        recipeService.incrementViews(recipeId);
        return ResponseEntity.noContent().build();
    }
}
