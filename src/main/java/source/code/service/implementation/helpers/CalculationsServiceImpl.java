package source.code.service.implementation.helpers;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.user.ActivityLevelType;
import source.code.helper.Enum.model.user.GenderType;
import source.code.helper.Enum.model.user.GoalType;
import source.code.model.activity.Activity;
import source.code.service.declaration.helpers.CalculationsService;

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
    public double calculateBMR(double weight, double height, int age, GenderType gender) {
        return switch (gender) {
            case MALE -> calculateBMRForMale(weight, height, age);
            case FEMALE -> calculateBMRForFemale(weight, height, age);
        };
    }

    @Override
    public double calculateTDEE(double bmr, ActivityLevelType activityLevel) {
        double activityFactor = getActivityFactor(activityLevel);
        return bmr * activityFactor;
    }

    @Override
    public double calculateCaloricNeeds(
            double weight,
            double height,
            int age,
            GenderType gender,
            ActivityLevelType activityLevel,
            GoalType goal
    ) {
        double bmr = calculateBMR(weight, height, age, gender);
        double tdee = calculateTDEE(bmr, activityLevel);
        return adjustCaloricNeedsBasedOnGoal(tdee, goal);
    }

    @Override
    public double calculateCaloriesBurned(int time, double weight, double met) {
        return (time * met * weight) / CALORIE_DIVISOR;
    }

    private double calculateBMRForMale(double weight, double height, int age) {
        return MALE_WEIGHT_MULTIPLIER * weight +
                MALE_HEIGHT_MULTIPLIER * height -
                MALE_AGE_MULTIPLIER * age +
                MALE_CONSTANT;
    }

    private double calculateBMRForFemale(double weight, double height, int age) {
        return FEMALE_WEIGHT_MULTIPLIER * weight +
                FEMALE_HEIGHT_MULTIPLIER * height -
                FEMALE_AGE_MULTIPLIER * age +
                FEMALE_CONSTANT;
    }

    private double getActivityFactor(ActivityLevelType activityLevel) {
        return switch (activityLevel) {
            case SEDENTARY -> SEDENTARY_FACTOR;
            case LIGHTLY_ACTIVE -> LIGHTLY_ACTIVE_FACTOR;
            case MODERATELY_ACTIVE -> MODERATELY_ACTIVE_FACTOR;
            case VERY_ACTIVE -> VERY_ACTIVE_FACTOR;
            case SUPER_ACTIVE -> SUPER_ACTIVE_FACTOR;
        };
    }

    private double adjustCaloricNeedsBasedOnGoal(double tdee, GoalType goal) {
        return switch (goal) {
            case MAINTAIN_WEIGHT -> tdee;
            case LOSE_WEIGHT -> tdee - CALORIE_DEFICIT;
            case BUILD_MUSCLE -> tdee + CALORIE_SURPLUS;
        };
    }
}
