package source.code.service.interfaces;

public interface WorkoutPlanService {
    void saveWorkoutToPlan(int workoutId, int planId);
    void deleteWorkoutFromPlan(int workoutId, int planId);
}