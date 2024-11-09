package source.code.service.declaration.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.model.food.Food;

import java.util.List;

public interface FoodService {
    FoodResponseDto createFood(FoodCreateDto request);

    void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteFood(int foodId);

    FoodResponseDto getFood(int foodId);

    List<FoodResponseDto> getAllFoods();

    List<FoodResponseDto> getFilteredFoods(FilterDto filter);

    List<Food> getAllFoodEntities();

    FoodCalculatedMacrosResponseDto calculateFoodMacros(int foodId, CalculateFoodMacrosRequestDto request);

    List<FoodResponseDto> getFoodsByCategory(int categoryId);
}
