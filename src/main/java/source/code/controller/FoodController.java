package source.code.controller;

import source.code.dto.request.CalculateFoodMacrosRequestDto;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedResponseDto;
import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.FoodService;
import source.code.service.UserFoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/foods")
public class FoodController {
    private final FoodService foodService;
    private final UserFoodService userFoodService;
    public FoodController(FoodService foodService, UserFoodService userFoodService) {
        this.foodService = foodService;
        this.userFoodService = userFoodService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDto> getFoodById(@PathVariable int id) {
        FoodResponseDto food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    @GetMapping
    public ResponseEntity<List<FoodResponseDto>> getAllFoods() {
        return ResponseEntity.ok(foodService.getFoods());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodResponseDto>> getFoodsByUserId(@PathVariable int userId) {
        List<FoodResponseDto> recipes = foodService.getFoodsByUserID(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> getLikesAndSavesFood(@PathVariable int id) {
        LikesAndSavesResponseDto dto = userFoodService.calculateLikesAndSavesByFoodId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/calculate-macros")
    public ResponseEntity<FoodCalculatedResponseDto> calculateFoodMacrosById(@PathVariable int id, @Valid @RequestBody CalculateFoodMacrosRequestDto request) {
        FoodCalculatedResponseDto response = foodService.calculateMacros(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FoodResponseDto> createFood(@Valid @RequestBody FoodCreateDto dto) {
        FoodResponseDto response = foodService.saveFood(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<FoodResponseDto>> searchFoods(@Valid @RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(foodService.searchFoods(request));
    }
}
