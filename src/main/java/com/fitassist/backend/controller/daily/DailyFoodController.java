package com.fitassist.backend.controller.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitassist.backend.annotation.DailyCartOwner;
import com.fitassist.backend.dto.request.food.DailyCartFoodCreateDto;
import com.fitassist.backend.dto.response.daily.DailyFoodsResponseDto;
import com.fitassist.backend.service.declaration.daily.DailyFoodService;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyFoodController {

	private final DailyFoodService dailyFoodService;

	public DailyFoodController(DailyFoodService dailyFoodService) {
		this.dailyFoodService = dailyFoodService;
	}

	@GetMapping("/{date}")
	public ResponseEntity<DailyFoodsResponseDto> getAllFoodsInCartByUser(@PathVariable LocalDate date) {
		DailyFoodsResponseDto cart = dailyFoodService.getFoodFromDailyCart(date);
		return ResponseEntity.ok(cart);
	}

	@PostMapping("/add/{foodId}")
	public ResponseEntity<Void> addDailyFoodToUser(@PathVariable int foodId,
			@Valid @RequestBody DailyCartFoodCreateDto request) {
		dailyFoodService.addFoodToDailyCart(foodId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DailyCartOwner
	@DeleteMapping("/remove/{dailyCartFoodId}")
	public ResponseEntity<Void> removeFoodFromDailyCartFood(@PathVariable int dailyCartFoodId) {
		dailyFoodService.removeFoodFromDailyCart(dailyCartFoodId);
		return ResponseEntity.noContent().build();
	}

	@DailyCartOwner
	@PatchMapping("/update/{dailyCartFoodId}")
	public ResponseEntity<Void> updateDailyCartFood(@PathVariable int dailyCartFoodId,
			@RequestBody JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		dailyFoodService.updateDailyFoodItem(dailyCartFoodId, patch);
		return ResponseEntity.noContent().build();
	}

}
