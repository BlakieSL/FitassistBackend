package com.fitassist.backend.service.declaration.daily;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.food.DailyCartFoodCreateDto;
import com.fitassist.backend.dto.response.daily.DailyFoodsResponseDto;
import jakarta.json.JsonMergePatch;

import java.time.LocalDate;

public interface DailyFoodService {

	void addFoodToDailyCart(int foodId, DailyCartFoodCreateDto dto);

	void removeFoodFromDailyCart(int dailyCartFoodId);

	void updateDailyFoodItem(int dailyCartFoodId, JsonMergePatch patch) throws JacksonException;

	DailyFoodsResponseDto getFoodFromDailyCart(LocalDate date);

}
