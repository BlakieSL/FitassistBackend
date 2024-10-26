package source.code.service.Declaration.Food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Food.CalculateFoodMacrosRequestDto;
import source.code.dto.Request.Food.FoodCreateDto;
import source.code.dto.Response.FoodCalculatedMacrosResponseDto;
import source.code.dto.Response.FoodResponseDto;

import java.util.List;

public interface FoodService {
  FoodResponseDto createFood(FoodCreateDto request);

  void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteFood(int foodId);

  FoodResponseDto getFood(int foodId);

  List<FoodResponseDto> getAllFoods();

  FoodCalculatedMacrosResponseDto calculateFoodMacros(int foodId, CalculateFoodMacrosRequestDto request);

  List<FoodResponseDto> getFoodsByCategory(int categoryId);
}
