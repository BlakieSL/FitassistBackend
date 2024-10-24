package source.code.controller.Recipe;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Recipe.RecipeCreateDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.service.declaration.Recipe.RecipeService;

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

  @PostMapping
  public ResponseEntity<RecipeResponseDto> createRecipe(
          @Valid @RequestBody RecipeCreateDto recipeDto) {
    RecipeResponseDto response = recipeService.createRecipe(recipeDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
