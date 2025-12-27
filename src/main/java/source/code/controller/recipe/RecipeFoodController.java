package source.code.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.recipe.RecipeOwnerOrAdmin;
import source.code.dto.request.recipe.FilterRecipesByFoodsDto;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.declaration.recipe.RecipeFoodService;

@RestController
@RequestMapping("/api/recipe-food")
public class RecipeFoodController {

	private final RecipeFoodService recipeFoodService;

	private final ObjectMapper objectMapper;

	public RecipeFoodController(RecipeFoodService recipeFoodService, ObjectMapper objectMapper) {
		this.recipeFoodService = recipeFoodService;
		this.objectMapper = objectMapper;
	}

	@RecipeOwnerOrAdmin
	@PostMapping("/{recipeId}/add/{foodId}")
	public ResponseEntity<Void> addFoodToRecipe(@PathVariable int recipeId, @PathVariable int foodId,
												@Valid @RequestBody RecipeFoodCreateDto request) {
		recipeFoodService.saveFoodToRecipe(recipeId, foodId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RecipeOwnerOrAdmin
	@DeleteMapping("/{recipeId}/remove/{foodId}")
	public ResponseEntity<Void> deleteFoodFromRecipe(@PathVariable int recipeId, @PathVariable int foodId) {
		recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);
		return ResponseEntity.ok().build();
	}

	@RecipeOwnerOrAdmin
	@PatchMapping("/{recipeId}/modify/{foodId}")
	public ResponseEntity<Void> updateFoodRecipe(@PathVariable int recipeId, @PathVariable int foodId,
												 @RequestBody JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		recipeFoodService.updateFoodRecipe(recipeId, foodId, patch);
		return ResponseEntity.noContent().build();
	}

	@RecipeOwnerOrAdmin
	@GetMapping("/{recipeId}/foods")
	public ResponseEntity<List<FoodSummaryDto>> getFoodsByRecipe(@PathVariable int recipeId) {
		List<FoodSummaryDto> foods = recipeFoodService.getFoodsByRecipe(recipeId);
		return ResponseEntity.ok(foods);
	}

	@PostMapping("/filter/foods")
	public ResponseEntity<Page<RecipeSummaryDto>> getRecipesByFoods(@Valid @RequestBody FilterRecipesByFoodsDto filter,
																	@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<RecipeSummaryDto> recipes = recipeFoodService.getRecipesByFoods(filter, pageable);
		return ResponseEntity.ok(recipes);
	}

}
