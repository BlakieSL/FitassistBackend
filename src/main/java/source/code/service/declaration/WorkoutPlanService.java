package source.code.service.declaration;

public interface WorkoutPlanService {
  void saveWorkoutToPlan(int workoutId, int planId);

  void deleteWorkoutFromPlan(int workoutId, int planId);
}