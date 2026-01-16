package com.fitassist.backend.service.declaration.helpers;

import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;

import java.math.BigDecimal;

public interface CalculationsService {

	BigDecimal calculateBMR(BigDecimal weight, BigDecimal height, int age, Gender gender);

	BigDecimal calculateTDEE(BigDecimal bmr, ActivityLevel activityLevel);

	BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal);

	BigDecimal calculateCaloriesBurned(int time, BigDecimal weight, BigDecimal met);

}
