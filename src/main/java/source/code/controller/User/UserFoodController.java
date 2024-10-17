package source.code.controller.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.implementation.User.UserFoodServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-foods")
public class UserFoodController {
  private final UserFoodServiceImpl userFoodService;

  public UserFoodController(UserFoodServiceImpl userFoodService) {
    this.userFoodService = userFoodService;
  }

  @GetMapping("/users/{userId}/type/{type}")
  public ResponseEntity<List<FoodResponseDto>> getFoodsByUserAndType(@PathVariable int userId,
                                                                     @PathVariable short type) {
    List<FoodResponseDto> recipes = userFoodService.getFoodsByUserAndType(userId, type);
    return ResponseEntity.ok(recipes);
  }

  @GetMapping("foods/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getFoodLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userFoodService.calculateFoodLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/users/{userId}/foods/{foodId}/type/{typeId}")
  public ResponseEntity<Void> saveFoodToUser(
          @PathVariable int userId, @PathVariable int foodId, @PathVariable short typeId) {

    userFoodService.saveFoodToUser(foodId, userId, typeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/users/{userId}/foods/{foodId}/type/{typeId}")
  public ResponseEntity<Void> deleteSavedFoodFromUser(
          @PathVariable int userId, @PathVariable int foodId, @PathVariable short typeId) {

    userFoodService.deleteSavedFoodFromUser(userId, foodId, typeId);
    return ResponseEntity.ok().build();
  }
}


