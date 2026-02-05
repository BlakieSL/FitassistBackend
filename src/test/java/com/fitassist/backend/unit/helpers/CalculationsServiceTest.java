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
	void calculateCaloriesBurned_shouldCalculateCaloriesBurned() {
		Short time = 30;
		BigDecimal weight = BigDecimal.valueOf(70);
		BigDecimal met = BigDecimal.valueOf(8);

		BigDecimal result = calculationsService.calculateCaloriesBurned(time, weight, met);

		assertEquals(BigDecimal.valueOf(294.0), result);
	}

}
