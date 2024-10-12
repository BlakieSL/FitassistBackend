package source.code.service.declaration;

public interface WorkoutPlanService {
  void addWorkoutToPlan(int workoutId, int planId);

  void deleteWorkoutFromPlan(int workoutId, int planId);
}