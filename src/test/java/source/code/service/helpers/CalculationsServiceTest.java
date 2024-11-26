package source.code.service.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.helpers.CalculationsServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CalculationsServiceTest {
    @InjectMocks
    private CalculationsServiceImpl calculationsService;

    private double weight;
    private double height;
    private int age;
    private String gender;
    private String activityLevel;
    private String goal;

    @BeforeEach
    void setUp() {
        weight = 70.0;
        height = 175.0;
        age = 25;
        gender = "male";
        activityLevel = "moderately_active";
        goal = "maintain";
    }

    @Test
    void calculateBMR_shouldReturnCorrectBMRForMale() {
        double bmr = calculationsService.calculateBMR(weight, height, age, gender);
        assertEquals(1673.75, bmr, 0.01);
    }

    @Test
    void calculateBMR_shouldReturnCorrectBMRForFemale() {
        gender = "female";
        double bmr = calculationsService.calculateBMR(weight, height, age, gender);
        assertEquals(1507.75, bmr);
    }

    @Test
    void calculateBMR_shouldReturnZeroForInvalidGender() {
        gender = "other";
        double bmr = calculationsService.calculateBMR(weight, height, age, gender);
        assertEquals(0, bmr);
    }

    @Test
    void calculateTDEE_shouldReturnCorrectTDEE() {
        double bmr = 1673.75;
        double tdee = calculationsService.calculateTDEE(bmr, activityLevel);
        assertEquals(2594.31, tdee, 0.01);
    }

    @Test
    void calculateCaloricNeeds_shouldReturnCorrectCaloriesForMaintainGoal() {
        double calories = calculationsService.calculateCaloricNeeds(
                weight,
                height,
                age,
                gender,
                activityLevel,
                goal
        );
        assertEquals(2594.31, calories, 0.01);
    }

    @Test
    void calculateCaloricNeeds_shouldReturnCorrectCaloriesForLoseGoal() {
        goal = "lose";
        double calories = calculationsService.calculateCaloricNeeds(
                weight,
                height,
                age,
                gender,
                activityLevel,
                goal
        );
        assertEquals(2394.31, calories, 0.01);
    }

    @Test
    void calculateCaloricNeeds_shouldReturnCorrectCaloriesForBuildGoal() {
        goal = "build";
        double calories = calculationsService.calculateCaloricNeeds(
                weight,
                height,
                age,
                gender,
                activityLevel,
                goal
        );
        assertEquals(2794.31, calories, 0.01);
    }

    @Test
    void calculateCaloriesBurned_shouldReturnCorrectCaloriesBurned() {
        int time = 30;
        double met = 8.0;
        double caloriesBurned = calculationsService.calculateCaloriesBurned(time, weight, met);
        assertEquals(84.0, caloriesBurned, 0.01);
    }
}