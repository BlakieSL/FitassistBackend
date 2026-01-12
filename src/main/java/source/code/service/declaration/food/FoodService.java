package source.code.service.declaration.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.model.food.Food;

import java.util.List;

public interface FoodService {

	FoodResponseDto createFood(FoodCreateDto request);

	void updateFood(int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteFood(int foodId);

	FoodResponseDto getFood(int foodId);

	Page<FoodSummaryDto> getFilteredFoods(FilterDto filter, Pageable pageable);

	List<Food> getAllFoodEntities();

	FoodCalculatedMacrosResponseDto calculateFoodMacros(int foodId, CalculateFoodMacrosRequestDto request);

}
