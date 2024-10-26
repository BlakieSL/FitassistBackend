package source.code.controller.Food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.Food.DailyFoodItemCreateDto;
import source.code.dto.Response.DailyFoodsResponseDto;
import source.code.service.Declaration.Daily.DailyFoodService;

@RestController
@RequestMapping(path = "/api/cart")
public class DailyFoodController {
  private final DailyFoodService dailyFoodService;

  public DailyFoodController(DailyFoodService dailyFoodService) {
    this.dailyFoodService = dailyFoodService;
  }

  @GetMapping("/{userId}")
  public ResponseEntity<DailyFoodsResponseDto> getAllFoodsInCartByUser(@PathVariable int userId) {
    DailyFoodsResponseDto cart = dailyFoodService.getFoodsFromDailyFoodItem(userId);
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/{userId}/add/{foodId}")
  public ResponseEntity<Void> addDailyFoodToUser(
          @PathVariable int userId,
          @PathVariable int foodId,
          @Valid @RequestBody DailyFoodItemCreateDto request) {

    dailyFoodService.addFoodToDailyFoodItem(userId, foodId, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{userId}/remove/{foodId}")
  public ResponseEntity<Void> removeFoodFromDailyCartFood(@PathVariable int userId,
                                                          @PathVariable int foodId) {

    dailyFoodService.removeFoodFromDailyFoodItem(userId, foodId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{userId}/modify-food/{foodId}")
  public ResponseEntity<Void> updateDailyCartFood(
          @PathVariable int userId,
          @PathVariable int foodId,
          @RequestBody JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    dailyFoodService.updateDailyFoodItem(userId, foodId, patch);
    return ResponseEntity.noContent().build();
  }
}
