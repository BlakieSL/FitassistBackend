package source.code.controller.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.food.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.service.declaration.daily.DailyFoodService;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyFoodController {
    private final DailyFoodService dailyFoodService;

    public DailyFoodController(DailyFoodService dailyFoodService) {
        this.dailyFoodService = dailyFoodService;
    }

    @GetMapping()
    public ResponseEntity<DailyFoodsResponseDto> getAllFoodsInCartByUser() {
        DailyFoodsResponseDto cart = dailyFoodService.getFoodFromDailyCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add/{foodId}")
    public ResponseEntity<Void> addDailyFoodToUser(
            @PathVariable int foodId,
            @Valid @RequestBody DailyFoodItemCreateDto request
    ) {
        dailyFoodService.addFoodToDailyCart(foodId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/remove/{foodId}")
    public ResponseEntity<Void> removeFoodFromDailyCartFood(@PathVariable int foodId) {
        dailyFoodService.removeFoodFromDailyCart(foodId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{foodId}")
    public ResponseEntity<Void> updateDailyCartFood(
            @PathVariable int foodId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        dailyFoodService.updateDailyFoodItem(foodId, patch);
        return ResponseEntity.noContent().build();
    }
}
