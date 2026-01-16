package com.fitassist.backend.service.declaration.food;

import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;

import java.util.List;

public interface FoodPopulationService {

	void populate(FoodResponseDto food);

	void populate(List<FoodSummaryDto> foods);

}
