package source.code.service.Declaration.Workout;

public interface WorkoutPlanService {
  void addWorkoutToPlan(int workoutId, int planId);

  void deleteWorkoutFromPlan(int workoutId, int planId);
}