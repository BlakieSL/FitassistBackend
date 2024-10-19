package source.code.service.declaration.Workout;

public interface WorkoutPlanService {
  void addWorkoutToPlan(int workoutId, int planId);

  void deleteWorkoutFromPlan(int workoutId, int planId);
}