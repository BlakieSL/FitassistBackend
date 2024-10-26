package source.code.service.Implementation.Helpers;

import org.springframework.stereotype.Service;
import source.code.service.Declaration.Helpers.CalculationsService;

@Service
public final class CalculationsServiceImpl implements CalculationsService {
  private static final double MALE_WEIGHT_MULTIPLIER = 10.0;
  private static final double MALE_HEIGHT_MULTIPLIER = 6.25;
  private static final double MALE_AGE_MULTIPLIER = 5.0;
  private static final double MALE_CONSTANT = 5.0;
  private static final double FEMALE_WEIGHT_MULTIPLIER = 10.0;
  private static final double FEMALE_HEIGHT_MULTIPLIER = 6.25;
  private static final double FEMALE_AGE_MULTIPLIER = 5.0;
  private static final double FEMALE_CONSTANT = -161.0;
  private static final double SEDENTARY_FACTOR = 1.2;
  private static final double LIGHTLY_ACTIVE_FACTOR = 1.375;
  private static final double MODERATELY_ACTIVE_FACTOR = 1.55;
  private static final double VERY_ACTIVE_FACTOR = 1.725;
  private static final double SUPER_ACTIVE_FACTOR = 1.9;
  private static final double DEFAULT_FACTOR = 1.2;
  private static final int CALORIE_DEFICIT = 200;
  private static final int CALORIE_SURPLUS = 200;
  private static final int CALORIE_DIVISOR = 200;

  @Override
  public double calculateBMR(double weight, double height, int age, String gender) {
    if ("male".equalsIgnoreCase(gender)) {
      return MALE_WEIGHT_MULTIPLIER * weight + MALE_HEIGHT_MULTIPLIER * height -
              MALE_AGE_MULTIPLIER * age + MALE_CONSTANT;
    } else if ("female".equalsIgnoreCase(gender)) {
      return FEMALE_WEIGHT_MULTIPLIER * weight + FEMALE_HEIGHT_MULTIPLIER * height -
              FEMALE_AGE_MULTIPLIER * age + FEMALE_CONSTANT;
    }
    return 0;
  }

  @Override
  public double calculateTDEE(double bmr, String activityLevel) {
    double activityFactor = switch (activityLevel.toLowerCase()) {
      case "sedentary" -> SEDENTARY_FACTOR;
      case "lightly_active" -> LIGHTLY_ACTIVE_FACTOR;
      case "moderately_active" -> MODERATELY_ACTIVE_FACTOR;
      case "very_active" -> VERY_ACTIVE_FACTOR;
      case "super_active" -> SUPER_ACTIVE_FACTOR;
      default -> DEFAULT_FACTOR;
    };
    return bmr * activityFactor;
  }

  @Override
  public double calculateCaloricNeeds(double weight, double height, int age, String gender,
                                      String activityLevel, String goal) {
    double bmr = calculateBMR(weight, height, age, gender);
    double tdee = calculateTDEE(bmr, activityLevel);
    return switch (goal.toLowerCase()) {
      case "maintain" -> tdee;
      case "lose" -> tdee - CALORIE_DEFICIT;
      case "build" -> tdee + CALORIE_SURPLUS;
      default -> tdee;
    };
  }

  @Override
  public double calculateCaloriesBurned(int time, double weight, double met) {
    return (time * met * weight) / CALORIE_DIVISOR;
  }
}