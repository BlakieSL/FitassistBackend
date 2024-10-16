package source.code.controller.Recipe;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.RecipeCreateDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.service.declaration.RecipeService;
import source.code.service.declaration.UserRecipeService;

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

  @GetMapping("/user/{userId}/type/{type}")
  public ResponseEntity<List<RecipeResponseDto>> getRecipesByUserAndType(@PathVariable int userId,
                                                                         @PathVariable short type) {
    List<RecipeResponseDto> recipes = recipeService.getRecipesByUserAndType(userId, type);
    return ResponseEntity.ok(recipes);
  }

  @GetMapping("/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getRecipeLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userRecipeService.calculateRecipeLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<RecipeResponseDto> createRecipe(
          @Valid @RequestBody RecipeCreateDto recipeDto) {
    RecipeResponseDto response = recipeService.createRecipe(recipeDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
