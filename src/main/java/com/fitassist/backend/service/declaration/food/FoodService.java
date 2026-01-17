package com.fitassist.backend.service.declaration.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.food.CalculateFoodMacrosRequestDto;
import com.fitassist.backend.dto.request.food.FoodCreateDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.model.food.Food;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
