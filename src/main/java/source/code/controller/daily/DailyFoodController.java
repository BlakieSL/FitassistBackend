package source.code.controller.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.DailyCartOwner;
import source.code.dto.request.food.DailyCartFoodCreateDto;
import source.code.dto.request.food.DailyCartFoodGetDto;
import source.code.dto.response.daily.DailyFoodsResponseDto;
import source.code.service.declaration.daily.DailyFoodService;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyFoodController {

	private final DailyFoodService dailyFoodService;

	public DailyFoodController(DailyFoodService dailyFoodService) {
		this.dailyFoodService = dailyFoodService;
	}

	@PostMapping
	public ResponseEntity<DailyFoodsResponseDto> getAllFoodsInCartByUser(
		@Valid @RequestBody DailyCartFoodGetDto request) {
		DailyFoodsResponseDto cart = dailyFoodService.getFoodFromDailyCart(request);
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
