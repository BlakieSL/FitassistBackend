package source.code.controller;

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
import source.code.annotation.AdminOnly;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.service.declaration.food.FoodService;

@RestController
@RequestMapping(path = "/api/foods")
public class FoodController {

	private final FoodService foodService;

	public FoodController(FoodService foodService) {
		this.foodService = foodService;
	}

	@AdminOnly
	@PostMapping
	public ResponseEntity<FoodResponseDto> createFood(@Valid @RequestBody FoodCreateDto dto) {
		FoodResponseDto response = foodService.createFood(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@AdminOnly
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateFood(@PathVariable int id, @RequestBody JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		foodService.updateFood(id, patch);
		return ResponseEntity.noContent().build();
	}

	@AdminOnly
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFood(@PathVariable int id) {
		foodService.deleteFood(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<FoodResponseDto> getFood(@PathVariable int id) {
		FoodResponseDto food = foodService.getFood(id);
		return ResponseEntity.ok(food);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<FoodSummaryDto>> getFilteredFoods(@Valid @RequestBody FilterDto filterDto,
																 @PageableDefault(size = 100, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<FoodSummaryDto> response = foodService.getFilteredFoods(filterDto, pageable);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/calculate-macros")
	public ResponseEntity<FoodCalculatedMacrosResponseDto> calculateFoodMacros(@PathVariable int id,
																			   @Valid @RequestBody CalculateFoodMacrosRequestDto request) {
		FoodCalculatedMacrosResponseDto response = foodService.calculateFoodMacros(id, request);
		return ResponseEntity.ok(response);
	}

}
