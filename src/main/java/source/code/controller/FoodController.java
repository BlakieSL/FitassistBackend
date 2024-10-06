package source.code.controller;

import source.code.dto.*;
import source.code.exception.ValidationException;
import source.code.service.FoodService;
import source.code.service.UserFoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<FoodDto> getFoodById(@PathVariable int id) {
        FoodDto food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    @GetMapping
    public ResponseEntity<List<FoodDto>> getAllFoods() {
        return ResponseEntity.ok(foodService.getFoods());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodDto>> getFoodsByUserId(@PathVariable int userId) {
        List<FoodDto> recipes = foodService.getFoodsByUserID(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavedDto> getLikesAndSavesFood(@PathVariable int id) {
        LikesAndSavedDto dto = userFoodService.calculateLikesAndSavesByFoodId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/calculate-macros")
    public ResponseEntity<FoodCalculatedDto> calculateFoodMacrosById(@PathVariable int id, @Valid @RequestBody CalculateAmountRequest request) {
        FoodCalculatedDto response = foodService.calculateMacros(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FoodDto> createFood(@Valid @RequestBody FoodAdditionDto dto) {
        FoodDto response = foodService.saveFood(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<FoodDto>> searchFoods(@Valid @RequestBody SearchDtoRequest request) {
        return ResponseEntity.ok(foodService.searchFoods(request));
    }
}
