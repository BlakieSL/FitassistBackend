package com.fitassist.backend.service.implementation.helpers;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.mapper.activity.ActivityMapper;
import com.fitassist.backend.mapper.daily.DailyActivityMapper;
import com.fitassist.backend.mapper.daily.DailyFoodMapper;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public final class CalculationsServiceImpl implements CalculationsService {

	private static final BigDecimal MET_CONSTANT = BigDecimal.valueOf(3.5);

	private static final BigDecimal MET_DIVISOR = BigDecimal.valueOf(200);

	private final DailyActivityMapper dailyActivityMapper;

	private final ActivityMapper activityMapper;

	private final DailyFoodMapper dailyFoodMapper;

	private final FoodMapper foodMapper;

	public CalculationsServiceImpl(DailyActivityMapper dailyActivityMapper, ActivityMapper activityMapper,
			DailyFoodMapper dailyFoodMapper, FoodMapper foodMapper) {
		this.dailyActivityMapper = dailyActivityMapper;
		this.activityMapper = activityMapper;
		this.dailyFoodMapper = dailyFoodMapper;
		this.foodMapper = foodMapper;
	}

	@Override
	public BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal) {
		BigDecimal bmr = gender.calculateBMR(weight, height, age);
		BigDecimal tdee = bmr.multiply(activityLevel.getActivityFactor());

		return goal.normalizeBasedOnGoal(tdee).setScale(1, RoundingMode.HALF_UP);
	}

	@Override
	public ActivityCalculatedResponseDto toCalculatedResponseDto(DailyCartActivity dailyCartActivity) {
		ActivityCalculatedResponseDto dto = dailyActivityMapper.toResponse(dailyCartActivity);
		BigDecimal caloriesBurned = calculateCaloriesBurned(dailyCartActivity.getTime(), dailyCartActivity.getWeight(),
				dailyCartActivity.getActivity().getMet());
		dto.setCaloriesBurned(caloriesBurned);

		return dto;
	}

	@Override
	public ActivityCalculatedResponseDto toCalculatedResponseDto(Activity activity, BigDecimal weight, Short time) {
		ActivityCalculatedResponseDto dto = activityMapper.toCalculated(activity);
		BigDecimal caloriesBurned = calculateCaloriesBurned(time, weight, activity.getMet());

		dto.setCaloriesBurned(caloriesBurned);
		dto.setTime(time);
		dto.setWeight(weight);

		return dto;
	}

	private BigDecimal calculateCaloriesBurned(Short time, BigDecimal weight, BigDecimal met) {
		return met.multiply(MET_CONSTANT)
			.multiply(weight)
			.multiply(BigDecimal.valueOf(time))
			.divide(MET_DIVISOR, 1, RoundingMode.HALF_UP);
	}

	@Override
	public FoodCalculatedMacrosResponseDto toCalculatedResponseDto(Food food, BigDecimal factor) {
		FoodCalculatedMacrosResponseDto dto = foodMapper.toCalculated(food);
		dto.setFoodMacros(calculateFoodMacros(food, factor));
		dto.setQuantity(factor.multiply(BigDecimal.valueOf(100)));

		return dto;
	}

	@Override
	public FoodCalculatedMacrosResponseDto toCalculatedResponseDto(DailyCartFood dailyCartFood) {
		Food food = dailyCartFood.getFood();
		BigDecimal quantity = dailyCartFood.getQuantity();
		BigDecimal factor = quantity.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

		FoodCalculatedMacrosResponseDto dto = dailyFoodMapper.toResponse(dailyCartFood);
		dto.setFoodMacros(calculateFoodMacros(food, factor));

		return dto;
	}

	private FoodMacros calculateFoodMacros(Food food, BigDecimal factor) {
		BigDecimal calories = food.getCalories().multiply(factor).setScale(1, RoundingMode.HALF_UP);
		BigDecimal protein = food.getProtein().multiply(factor).setScale(2, RoundingMode.HALF_UP);
		BigDecimal fat = food.getFat().multiply(factor).setScale(2, RoundingMode.HALF_UP);
		BigDecimal carbohydrates = food.getCarbohydrates().multiply(factor).setScale(2, RoundingMode.HALF_UP);

		return FoodMacros.of(calories, protein, fat, carbohydrates);
	}

}
