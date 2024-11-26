package source.code.service.declaration.helpers;

import source.code.helper.Enum.model.user.ActivityLevelType;
import source.code.helper.Enum.model.user.GenderType;
import source.code.helper.Enum.model.user.GoalType;

public interface CalculationsService {
    double calculateBMR(double weight, double height, int age, GenderType gender);

    double calculateTDEE(double bmr, ActivityLevelType activityLevel);

    double calculateCaloricNeeds(double weight, double height, int age, GenderType gender,
                                 ActivityLevelType activityLevel, GoalType goal);

    double calculateCaloriesBurned(int time, double weight, double met);
}
