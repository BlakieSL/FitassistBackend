package source.code.controller.Food;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.CalculateFoodMacrosRequestDto;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.declaration.FoodService;
import source.code.service.declaration.UserFoodService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/foods")
public class FoodController {
  private final FoodService foodService;

  public FoodController(FoodService foodService) {
    this.foodService = foodService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<FoodResponseDto> getFood(@PathVariable int id) {
    FoodResponseDto food = foodService.getFood(id);
    return ResponseEntity.ok(food);
  }

  @GetMapping
  public ResponseEntity<List<FoodResponseDto>> getAllFoods() {
    return ResponseEntity.ok(foodService.getAllFoods());
  }

  @PostMapping("/{id}/calculate-macros")
  public ResponseEntity<FoodCalculatedMacrosResponseDto> calculateFoodMacros(
          @PathVariable int id,
          @Valid @RequestBody CalculateFoodMacrosRequestDto request) {

    FoodCalculatedMacrosResponseDto response = foodService.calculateFoodMacros(id, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<FoodResponseDto> createFood(@Valid @RequestBody FoodCreateDto dto) {
    FoodResponseDto response = foodService.createFood(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/search")
  public ResponseEntity<List<FoodResponseDto>> searchFoods(
          @Valid @RequestBody SearchRequestDto request) {

    return ResponseEntity.ok(foodService.searchFoods(request));
  }
}
