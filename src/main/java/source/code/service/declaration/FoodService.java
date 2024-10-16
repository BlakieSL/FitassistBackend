package source.code.service.declaration;

import source.code.dto.request.CalculateFoodMacrosRequestDto;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;
import source.code.dto.response.FoodResponseDto;

import java.util.List;

public interface FoodService {
  FoodResponseDto createFood(FoodCreateDto request);

  FoodResponseDto getFood(int id);

  List<FoodResponseDto> getAllFoods();

  FoodCalculatedMacrosResponseDto calculateFoodMacros(int id, CalculateFoodMacrosRequestDto request);

  List<FoodResponseDto> searchFoods(SearchRequestDto request);

  List<FoodResponseDto> getFoodsByUserAndType(int userId, short type);

  List<FoodCategoryResponseDto> getAllCategories();

  List<FoodResponseDto> getFoodsByCategory(int categoryId);
}
