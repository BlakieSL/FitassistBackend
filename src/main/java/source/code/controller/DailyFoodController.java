package source.code.controller;

import source.code.dto.request.DailyCartFoodCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.service.DailyFoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyFoodController {
    private final DailyFoodService dailyFoodService;
    public DailyFoodController(DailyFoodService dailyFoodService) {
        this.dailyFoodService = dailyFoodService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DailyFoodsResponseDto> getAllFoodsInCartByUserID(@PathVariable int userId) {
        DailyFoodsResponseDto cart = dailyFoodService.getFoodsInCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add/{foodId}")
    public ResponseEntity<Void> addFoodToCartByUserId(
            @PathVariable int userId,
            @PathVariable int foodId,
            @Valid @RequestBody DailyCartFoodCreateDto request) {

        dailyFoodService.addFoodToCart(userId, foodId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{foodId}")
    public ResponseEntity<Void> removeFoodFromCart(@PathVariable int userId, @PathVariable int foodId) {
        dailyFoodService.removeFoodFromCart(userId, foodId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-food/{foodId}")
    public ResponseEntity<Void> modifyDailyCartFood(
            @PathVariable int userId,
            @PathVariable int foodId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {

        dailyFoodService.modifyDailyCartFood(userId, foodId, patch);
        return ResponseEntity.noContent().build();
    }
}
