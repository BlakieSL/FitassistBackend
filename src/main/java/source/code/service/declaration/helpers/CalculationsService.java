package source.code.service.declaration.helpers;

import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Gender;
import source.code.helper.Enum.model.user.Goal;

import java.math.BigDecimal;

public interface CalculationsService {

	BigDecimal calculateBMR(BigDecimal weight, BigDecimal height, int age, Gender gender);

	BigDecimal calculateTDEE(BigDecimal bmr, ActivityLevel activityLevel);

	BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal);

	BigDecimal calculateCaloriesBurned(int time, BigDecimal weight, BigDecimal met);

}
