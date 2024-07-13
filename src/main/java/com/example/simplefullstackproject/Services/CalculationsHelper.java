package com.example.simplefullstackproject.Services;

import org.springframework.stereotype.Service;

@Service
public final class CalculationsHelper {
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

    public double calculateBMR(final double weight, final double height,
                               final int age, final String gender) {
        if ("male".equalsIgnoreCase(gender)) {
            return MALE_WEIGHT_MULTIPLIER * weight + MALE_HEIGHT_MULTIPLIER * height -
                    MALE_AGE_MULTIPLIER * age + MALE_CONSTANT;
        } else if ("female".equalsIgnoreCase(gender)) {
            return FEMALE_WEIGHT_MULTIPLIER * weight + FEMALE_HEIGHT_MULTIPLIER * height -
                    FEMALE_AGE_MULTIPLIER * age + FEMALE_CONSTANT;
        }
        return 0;
    }

    public double calculateTDEE(final double bmr, final String activityLevel) {
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

    public double calculateCaloricNeeds(final double weight, final double height,
                                        final int age, final String gender,
                                        final String activityLevel, final String goal) {
        double bmr = calculateBMR(weight, height, age, gender);
        double tdee = calculateTDEE(bmr, activityLevel);
        return switch (goal.toLowerCase()) {
            case "maintain" -> tdee;
            case "lose" -> tdee - CALORIE_DEFICIT;
            case "build" -> tdee + CALORIE_SURPLUS;
            default -> tdee;
        };
    }

    public double calculateCaloriesBurned(
            final int time, final double weight, final double met) {
        return (time * met * weight) / CALORIE_DIVISOR;
    }
}