package source.code.service.implementation.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Gender;
import source.code.helper.Enum.model.user.Goal;
import source.code.service.declaration.helpers.CalculationsService;

@Service
public final class CalculationsServiceImpl implements CalculationsService {

	private static final BigDecimal MALE_WEIGHT_MULTIPLIER = BigDecimal.valueOf(10.0);

	private static final BigDecimal MALE_HEIGHT_MULTIPLIER = BigDecimal.valueOf(6.25);

	private static final BigDecimal MALE_AGE_MULTIPLIER = BigDecimal.valueOf(5.0);

	private static final BigDecimal MALE_CONSTANT = BigDecimal.valueOf(5.0);

	private static final BigDecimal FEMALE_WEIGHT_MULTIPLIER = BigDecimal.valueOf(10.0);

	private static final BigDecimal FEMALE_HEIGHT_MULTIPLIER = BigDecimal.valueOf(6.25);

	private static final BigDecimal FEMALE_AGE_MULTIPLIER = BigDecimal.valueOf(5.0);

	private static final BigDecimal FEMALE_CONSTANT = BigDecimal.valueOf(-161.0);

	private static final BigDecimal SEDENTARY_FACTOR = BigDecimal.valueOf(1.2);

	private static final BigDecimal LIGHTLY_ACTIVE_FACTOR = BigDecimal.valueOf(1.375);

	private static final BigDecimal MODERATELY_ACTIVE_FACTOR = BigDecimal.valueOf(1.55);

	private static final BigDecimal VERY_ACTIVE_FACTOR = BigDecimal.valueOf(1.725);

	private static final BigDecimal SUPER_ACTIVE_FACTOR = BigDecimal.valueOf(1.9);

	private static final BigDecimal CALORIE_DEFICIT = BigDecimal.valueOf(200);

	private static final BigDecimal CALORIE_SURPLUS = BigDecimal.valueOf(200);

	private static final BigDecimal CALORIE_DIVISOR = BigDecimal.valueOf(200);

	private static final int CALCULATION_SCALE = 10;

	private static final int RESULT_SCALE = 2;

	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

	@Override
	public BigDecimal calculateBMR(BigDecimal weight, BigDecimal height, int age, Gender gender) {
		return switch (gender) {
			case MALE -> calculateBMRForMale(weight, height, age);
			case FEMALE -> calculateBMRForFemale(weight, height, age);
		};
	}

	@Override
	public BigDecimal calculateTDEE(BigDecimal bmr, ActivityLevel activityLevel) {
		BigDecimal activityFactor = getActivityFactor(activityLevel);
		return bmr.multiply(activityFactor).setScale(RESULT_SCALE, ROUNDING_MODE);
	}

	@Override
	public BigDecimal calculateCaloricNeeds(BigDecimal weight, BigDecimal height, int age, Gender gender,
			ActivityLevel activityLevel, Goal goal) {
		BigDecimal bmr = calculateBMR(weight, height, age, gender);
		BigDecimal tdee = calculateTDEE(bmr, activityLevel);
		return adjustCaloricNeedsBasedOnGoal(tdee, goal).setScale(RESULT_SCALE, ROUNDING_MODE);
	}

	@Override
	public BigDecimal calculateCaloriesBurned(int time, BigDecimal weight, BigDecimal met) {
		BigDecimal timeBD = BigDecimal.valueOf(time);
		return timeBD.multiply(met)
			.multiply(weight)
			.divide(CALORIE_DIVISOR, CALCULATION_SCALE, ROUNDING_MODE)
			.setScale(RESULT_SCALE, ROUNDING_MODE);
	}

	private BigDecimal calculateBMRForMale(BigDecimal weight, BigDecimal height, int age) {
		BigDecimal ageBD = BigDecimal.valueOf(age);
		return MALE_WEIGHT_MULTIPLIER.multiply(weight)
			.add(MALE_HEIGHT_MULTIPLIER.multiply(height))
			.subtract(MALE_AGE_MULTIPLIER.multiply(ageBD))
			.add(MALE_CONSTANT);
	}

	private BigDecimal calculateBMRForFemale(BigDecimal weight, BigDecimal height, int age) {
		BigDecimal ageBD = BigDecimal.valueOf(age);
		return FEMALE_WEIGHT_MULTIPLIER.multiply(weight)
			.add(FEMALE_HEIGHT_MULTIPLIER.multiply(height))
			.subtract(FEMALE_AGE_MULTIPLIER.multiply(ageBD))
			.add(FEMALE_CONSTANT);
	}

	private BigDecimal getActivityFactor(ActivityLevel activityLevel) {
		return switch (activityLevel) {
			case SEDENTARY -> SEDENTARY_FACTOR;
			case LIGHTLY_ACTIVE -> LIGHTLY_ACTIVE_FACTOR;
			case MODERATELY_ACTIVE -> MODERATELY_ACTIVE_FACTOR;
			case VERY_ACTIVE -> VERY_ACTIVE_FACTOR;
			case SUPER_ACTIVE -> SUPER_ACTIVE_FACTOR;
		};
	}

	private BigDecimal adjustCaloricNeedsBasedOnGoal(BigDecimal tdee, Goal goal) {
		return switch (goal) {
			case MAINTAIN_WEIGHT -> tdee;
			case LOSE_WEIGHT -> tdee.subtract(CALORIE_DEFICIT);
			case BUILD_MUSCLE -> tdee.add(CALORIE_SURPLUS);
		};
	}

}
