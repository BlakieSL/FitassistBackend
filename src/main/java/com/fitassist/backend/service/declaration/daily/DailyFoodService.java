package com.fitassist.backend.service.declaration.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.food.DailyCartFoodCreateDto;
import com.fitassist.backend.dto.response.daily.DailyFoodsResponseDto;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.time.LocalDate;

public interface DailyFoodService {

	void addFoodToDailyCart(int foodId, DailyCartFoodCreateDto dto);

	void removeFoodFromDailyCart(int dailyCartFoodId);

	void updateDailyFoodItem(int dailyCartFoodId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException;

	DailyFoodsResponseDto getFoodFromDailyCart(LocalDate date);

}
