package com.example.simplefullstackproject.Services;

import org.springframework.stereotype.Service;

@Service
public class CalculationsHelper {
    public double calculateBMR(double weight, double height, int age, String gender) {
        if ("male".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height - 5 * age + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return 0;
    }

    public double calculateTDEE(double bmr, String activityLevel) {
        double activityFactor = switch (activityLevel.toLowerCase()) {
            case "sedentary" -> 1.2;
            case "lightly_active" -> 1.375;
            case "moderately_active" -> 1.55;
            case "very_active" -> 1.725;
            case "super_active" -> 1.9;
            default -> 1.2;
        };
        return bmr * activityFactor;
    }

    public double calculateCaloricNeeds(double weight, double height, int age, String gender, String activityLevel, String goal) {
        double bmr = calculateBMR(weight, height, age, gender);
        double tdee = calculateTDEE(bmr, activityLevel);
        return switch (goal.toLowerCase()) {
            case "maintain" -> tdee;
            case "lose" -> tdee - 200;
            case "build" -> tdee + 200;
            default -> tdee;
        };
    }

    public double calculateCaloriesBurned(int time, double weight, double met){
        return (time * met * weight)/200;
    }
}
