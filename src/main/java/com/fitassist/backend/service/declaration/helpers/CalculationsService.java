package com.fitassist.backend.service.declaration.helpers;

import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;

import java.math.BigDecimal;

public interface CalculationsService {

	BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal);

	BigDecimal calculateCaloriesBurned(Short time, BigDecimal weight, BigDecimal met);

	ActivityCalculatedResponseDto toCalculatedResponseDto(DailyCartActivity dailyCartActivity);

	ActivityCalculatedResponseDto toCalculatedResponseDto(Activity activity, BigDecimal weight, Short time);

}
