package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.CalculateFoodMacrosRequestDto;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.request.FoodUpdateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;
import source.code.dto.response.FoodResponseDto;

import java.util.List;

public interface FoodService {
  FoodResponseDto createFood(FoodCreateDto request);

  void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteFood(int foodId);

  FoodResponseDto getFood(int foodId);

  List<FoodResponseDto> getAllFoods();

  FoodCalculatedMacrosResponseDto calculateFoodMacros(int foodId, CalculateFoodMacrosRequestDto request);

  List<FoodResponseDto> searchFoods(SearchRequestDto request);

  List<FoodResponseDto> getFoodsByUserAndType(int userId, short type);

  List<FoodCategoryResponseDto> getAllCategories();

  List<FoodResponseDto> getFoodsByCategory(int categoryId);
}
