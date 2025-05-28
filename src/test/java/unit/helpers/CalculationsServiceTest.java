package unit.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.model.user.ActivityLevelType;
import source.code.helper.Enum.model.user.GenderType;
import source.code.helper.Enum.model.user.GoalType;
import source.code.service.implementation.helpers.CalculationsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CalculationsServiceTest {

    private CalculationsServiceImpl calculationsService;

    @BeforeEach
    void setUp() {
        calculationsService = new CalculationsServiceImpl();
    }

    @Test
    void calculateBMR_shouldCalculateBMRForMale() {
        double weight = 70.0;
        double height = 175.0;
        int age = 25;
        GenderType gender = GenderType.MALE;

        double result = calculationsService.calculateBMR(weight, height, age, gender);

        assertEquals(1673.75, result);
    }

    @Test
    void calculateBMR_shouldCalculateBMRForFemale() {
        double weight = 60.0;
        double height = 165.0;
        int age = 30;
        GenderType gender = GenderType.FEMALE;

        double result = calculationsService.calculateBMR(weight, height, age, gender);

        assertEquals(1320.25, result);
    }

    @Test
    void calculateTDEE_shouldCalculateTDEEForSedentary() {
        double bmr = 1705.0;
        ActivityLevelType activityLevel = ActivityLevelType.SEDENTARY;

        double result = calculationsService.calculateTDEE(bmr, activityLevel);

        assertEquals(2046.0, result);
    }

    @Test
    void calculateTDEE_shouldCalculateTDEEForSuperActive() {
        double bmr = 1705.0;
        ActivityLevelType activityLevel = ActivityLevelType.SUPER_ACTIVE;

        double result = calculationsService.calculateTDEE(bmr, activityLevel);

        assertEquals(3239.5, result);
    }

    @Test
    void calculateCaloricNeeds_shouldCalculateCaloricNeedsForLosingWeight() {
        double weight = 70.0;
        double height = 175.0;
        int age = 25;
        GenderType gender = GenderType.MALE;
        ActivityLevelType activityLevel = ActivityLevelType.MODERATELY_ACTIVE;
        GoalType goal = GoalType.LOSE_WEIGHT;

        double result = calculationsService
                .calculateCaloricNeeds(weight, height, age, gender, activityLevel, goal);

        assertEquals(2394.3125, result);
    }

    @Test
    void calculateCaloricNeeds_shouldCalculateCaloricNeedsForBuildingMuscle() {
        double weight = 60.0;
        double height = 165.0;
        int age = 30;
        GenderType gender = GenderType.FEMALE;
        ActivityLevelType activityLevel = ActivityLevelType.VERY_ACTIVE;
        GoalType goal = GoalType.BUILD_MUSCLE;

        double result = calculationsService
                .calculateCaloricNeeds(weight, height, age, gender, activityLevel, goal);

        assertEquals(2477.43125, result);
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCaloriesBurned() {
        int time = 30;
        double weight = 70.0;
        double met = 8.0;

        double result = calculationsService.calculateCaloriesBurned(time, weight, met);

        assertEquals(84.0, result);
    }
}