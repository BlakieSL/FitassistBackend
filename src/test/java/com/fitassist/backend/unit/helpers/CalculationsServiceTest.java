package com.fitassist.backend.unit.helpers;

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
import com.fitassist.backend.service.implementation.helpers.CalculationsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculationsServiceTest {

	@Mock
	private DailyActivityMapper dailyActivityMapper;

	@Mock
	private ActivityMapper activityMapper;

	@Mock
	private DailyFoodMapper dailyFoodMapper;

	@Mock
	private FoodMapper foodMapper;

	@InjectMocks
	private CalculationsServiceImpl calculationsService;

	@Test
	void calculateCaloricNeeds_shouldCalculateCaloricNeedsForLosingWeight() {
		BigDecimal weight = BigDecimal.valueOf(70);
		BigDecimal height = BigDecimal.valueOf(175);
		int age = 25;
		Gender gender = Gender.MALE;
		ActivityLevel activityLevel = ActivityLevel.MODERATELY_ACTIVE;
		Goal goal = Goal.LOSE_WEIGHT;

		BigDecimal result = calculationsService.calculateCaloricNeeds(weight, height, age, gender, activityLevel, goal);

		assertEquals(BigDecimal.valueOf(2394.3), result);
	}

	@Test
	void calculateCaloricNeeds_shouldCalculateCaloricNeedsForBuildingMuscle() {
		BigDecimal weight = BigDecimal.valueOf(60);
		BigDecimal height = BigDecimal.valueOf(165);
		int age = 30;
		Gender gender = Gender.FEMALE;
		ActivityLevel activityLevel = ActivityLevel.VERY_ACTIVE;
		Goal goal = Goal.BUILD_MUSCLE;

		BigDecimal result = calculationsService.calculateCaloricNeeds(weight, height, age, gender, activityLevel, goal);

		assertEquals(BigDecimal.valueOf(2477.4), result);
	}

	@Test
	void toCalculatedResponseDto_shouldCalculateActivityFromDailyCart() {
		DailyCartActivity dailyCartActivity = new DailyCartActivity();
		Activity activity = new Activity();
		activity.setMet(BigDecimal.valueOf(8));
		dailyCartActivity.setActivity(activity);
		dailyCartActivity.setTime((short) 30);
		dailyCartActivity.setWeight(BigDecimal.valueOf(70));
		ActivityCalculatedResponseDto dto = new ActivityCalculatedResponseDto();

		when(dailyActivityMapper.toResponse(any(DailyCartActivity.class))).thenReturn(dto);

		ActivityCalculatedResponseDto result = calculationsService.toCalculatedResponseDto(dailyCartActivity);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(294.0), result.getCaloriesBurned());
	}

	@Test
	void toCalculatedResponseDto_shouldCalculateActivityWithParams() {
		Activity activity = new Activity();
		activity.setMet(BigDecimal.valueOf(8));
		BigDecimal weight = BigDecimal.valueOf(70);
		Short time = 30;
		ActivityCalculatedResponseDto dto = new ActivityCalculatedResponseDto();

		when(activityMapper.toCalculated(any(Activity.class))).thenReturn(dto);

		ActivityCalculatedResponseDto result = calculationsService.toCalculatedResponseDto(activity, weight, time);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(294.0), result.getCaloriesBurned());
		assertEquals(time, result.getTime());
		assertEquals(weight, result.getWeight());
	}

	@Test
	void toCalculatedResponseDto_shouldCalculateFoodMacrosWithFactor() {
		Food food = new Food();
		food.setCalories(BigDecimal.valueOf(100));
		food.setProtein(BigDecimal.valueOf(10));
		food.setFat(BigDecimal.valueOf(5));
		food.setCarbohydrates(BigDecimal.valueOf(15));
		BigDecimal factor = BigDecimal.valueOf(2.5);

		FoodCalculatedMacrosResponseDto dto = new FoodCalculatedMacrosResponseDto();

		when(foodMapper.toCalculated(any(Food.class))).thenReturn(dto);

		FoodCalculatedMacrosResponseDto result = calculationsService.toCalculatedResponseDto(food, factor);

		assertNotNull(result);
		assertNotNull(result.getFoodMacros());
		assertEquals(BigDecimal.valueOf(250.0).setScale(1), result.getFoodMacros().getCalories());
		assertEquals(BigDecimal.valueOf(25.00).setScale(2), result.getFoodMacros().getProtein());
		assertEquals(BigDecimal.valueOf(12.50).setScale(2), result.getFoodMacros().getFat());
		assertEquals(BigDecimal.valueOf(37.50).setScale(2), result.getFoodMacros().getCarbohydrates());
		assertEquals(BigDecimal.valueOf(250.0), result.getQuantity());
	}

	@Test
	void toCalculatedResponseDto_shouldCalculateFoodMacrosFromDailyCart() {
		Food food = new Food();
		food.setCalories(BigDecimal.valueOf(100));
		food.setProtein(BigDecimal.valueOf(10));
		food.setFat(BigDecimal.valueOf(5));
		food.setCarbohydrates(BigDecimal.valueOf(15));

		DailyCartFood dailyCartFood = new DailyCartFood();
		dailyCartFood.setFood(food);
		dailyCartFood.setQuantity(BigDecimal.valueOf(250));

		FoodCalculatedMacrosResponseDto dto = new FoodCalculatedMacrosResponseDto();

		when(dailyFoodMapper.toResponse(any(DailyCartFood.class))).thenReturn(dto);

		FoodCalculatedMacrosResponseDto result = calculationsService.toCalculatedResponseDto(dailyCartFood);

		assertNotNull(result);
		assertNotNull(result.getFoodMacros());
		assertEquals(BigDecimal.valueOf(250.0).setScale(1), result.getFoodMacros().getCalories());
		assertEquals(BigDecimal.valueOf(25.00).setScale(2), result.getFoodMacros().getProtein());
		assertEquals(BigDecimal.valueOf(12.50).setScale(2), result.getFoodMacros().getFat());
		assertEquals(BigDecimal.valueOf(37.50).setScale(2), result.getFoodMacros().getCarbohydrates());
	}

}
