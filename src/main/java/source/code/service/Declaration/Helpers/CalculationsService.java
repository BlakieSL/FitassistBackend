package source.code.service.Declaration.Helpers;

public interface CalculationsService {
  double calculateBMR(double weight, double height, int age, String gender);
  double calculateTDEE(double bmr, String activityLevel);
  double calculateCaloricNeeds(double weight, double height, int age, String gender,
                               String activityLevel, String goal);
  double calculateCaloriesBurned(int time, double weight, double met);
}
