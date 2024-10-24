package source.code.controller.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Recipe.RecipeFoodCreateDto;
import source.code.service.declaration.Recipe.RecipeFoodService;

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
          @Valid @RequestBody RecipeFoodCreateDto request) {

    recipeFoodService.saveFoodToRecipe(recipeId, foodId, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{recipeId}/remove/{foodId}")
  public ResponseEntity<Void> deleteFoodFromRecipe(@PathVariable int recipeId,
                                                   @PathVariable int foodId) {
    recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{recipeId}/modify/{foodId}")
  public ResponseEntity<Void> updateFoodRecipe(
          @PathVariable int recipeId,
          @PathVariable int foodId,
          @Valid @RequestBody JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    recipeFoodService.updateFoodRecipe(recipeId, foodId, patch);
    return ResponseEntity.noContent().build();
  }
}
