package source.code.controller;

import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;
import source.code.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/food-categories")
public class FoodCategoryController {
    private final FoodService foodService;

    public FoodCategoryController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodCategoryResponseDto>> getAllFoodCategories() {
        return ResponseEntity.ok(foodService.getCategories());
    }

    @GetMapping("/{categoryId}/foods")
    public ResponseEntity<List<FoodResponseDto>> getFoodsByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(foodService.getFoodsByCategory(categoryId));
    }
}