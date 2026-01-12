package source.code.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.recipe.RecipeOwnerOrAdminOrModerator;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.service.declaration.recipe.RecipeFoodService;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-food")
public class RecipeFoodController {

	private final RecipeFoodService recipeFoodService;

	public RecipeFoodController(RecipeFoodService recipeFoodService) {
		this.recipeFoodService = recipeFoodService;
	}

	@RecipeOwnerOrAdminOrModerator
	@PostMapping("/{recipeId}/add")
	public ResponseEntity<Void> addFoodToRecipe(@PathVariable int recipeId,
			@Valid @RequestBody RecipeFoodCreateDto request) {
		recipeFoodService.saveFoodToRecipe(recipeId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RecipeOwnerOrAdminOrModerator
	@PostMapping("/{recipeId}/replaceAll")
	public ResponseEntity<Void> replaceAllFoodsInRecipe(@PathVariable int recipeId,
			@Valid @RequestBody RecipeFoodCreateDto request) {
		recipeFoodService.replaceAllFoodsInRecipe(recipeId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RecipeOwnerOrAdminOrModerator
	@DeleteMapping("/{recipeId}/remove/{foodId}")
	public ResponseEntity<Void> deleteFoodFromRecipe(@PathVariable int recipeId, @PathVariable int foodId) {
		recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);
		return ResponseEntity.ok().build();
	}

	@RecipeOwnerOrAdminOrModerator
	@PatchMapping("/{recipeId}/modify/{foodId}")
	public ResponseEntity<Void> updateFoodRecipe(@PathVariable int recipeId, @PathVariable int foodId,
			@RequestBody JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		recipeFoodService.updateFoodRecipe(recipeId, foodId, patch);
		return ResponseEntity.noContent().build();
	}

	@RecipeOwnerOrAdminOrModerator
	@GetMapping("/{recipeId}/foods")
	public ResponseEntity<List<FoodSummaryDto>> getFoodsByRecipe(@PathVariable int recipeId) {
		List<FoodSummaryDto> foods = recipeFoodService.getFoodsByRecipe(recipeId);
		return ResponseEntity.ok(foods);
	}

}
