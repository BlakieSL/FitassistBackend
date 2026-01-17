package com.fitassist.backend.unit.helpers;

import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;
import com.fitassist.backend.service.implementation.helpers.CalculationsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CalculationsServiceTest {

	@InjectMocks
	private CalculationsServiceImpl calculationsService;

	@Test
	void calculateBMR_shouldCalculateBMRForMale() {
		BigDecimal weight = BigDecimal.valueOf(70);
		BigDecimal height = BigDecimal.valueOf(175);
		int age = 25;
		Gender gender = Gender.MALE;

		BigDecimal result = calculationsService.calculateBMR(weight, height, age, gender);

		assertEquals(new BigDecimal("1673.75"), result);
	}

	@Test
	void calculateBMR_shouldCalculateBMRForFemale() {
		BigDecimal weight = BigDecimal.valueOf(60);
		BigDecimal height = BigDecimal.valueOf(165);
		int age = 30;
		Gender gender = Gender.FEMALE;

		BigDecimal result = calculationsService.calculateBMR(weight, height, age, gender);

		assertEquals(new BigDecimal("1320.25"), result);
	}

	@Test
	void calculateTDEE_shouldCalculateTDEEForSedentary() {
		BigDecimal bmr = BigDecimal.valueOf(1705);
		ActivityLevel activityLevel = ActivityLevel.SEDENTARY;

		BigDecimal result = calculationsService.calculateTDEE(bmr, activityLevel);

		assertEquals(new BigDecimal("2046.00"), result);
	}

	@Test
	void calculateTDEE_shouldCalculateTDEEForSuperActive() {
		BigDecimal bmr = BigDecimal.valueOf(1705);
		ActivityLevel activityLevel = ActivityLevel.SUPER_ACTIVE;

		BigDecimal result = calculationsService.calculateTDEE(bmr, activityLevel);

		assertEquals(new BigDecimal("3239.50"), result);
	}

	@Test
	void calculateCaloricNeeds_shouldCalculateCaloricNeedsForLosingWeight() {
		BigDecimal weight = BigDecimal.valueOf(70);
		BigDecimal height = BigDecimal.valueOf(175);
		int age = 25;
		Gender gender = Gender.MALE;
		ActivityLevel activityLevel = ActivityLevel.MODERATELY_ACTIVE;
		Goal goal = Goal.LOSE_WEIGHT;

		BigDecimal result = calculationsService.calculateCaloricNeeds(weight, height, age, gender, activityLevel, goal);

		assertEquals(new BigDecimal("2394.31"), result);
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

		assertEquals(new BigDecimal("2477.43"), result);
	}

	@Test
	void calculateCaloriesBurned_shouldCalculateCaloriesBurned() {
		int time = 30;
		BigDecimal weight = BigDecimal.valueOf(70);
		BigDecimal met = BigDecimal.valueOf(8);

		BigDecimal result = calculationsService.calculateCaloriesBurned(time, weight, met);

		assertEquals(new BigDecimal("84.00"), result);
	}

}
