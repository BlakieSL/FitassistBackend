package com.fitassist.backend.service.implementation.helpers;

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

	@Override
	public BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal) {
		BigDecimal bmr = gender.calculateBMR(weight, height, age);
		BigDecimal tdee = bmr.multiply(activityLevel.getActivityFactor()); // Total Daily
																			// Energy
																			// Expenditure

		return goal.normalizeBasedOnGoal(tdee).setScale(1, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateCaloriesBurned(Short time, BigDecimal weight, BigDecimal met) {
		return met.multiply(MET_CONSTANT)
			.multiply(weight)
			.multiply(BigDecimal.valueOf(time))
			.divide(MET_DIVISOR, 1, RoundingMode.HALF_UP);
	}

}
