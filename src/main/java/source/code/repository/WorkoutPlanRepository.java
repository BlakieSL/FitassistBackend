package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Workout.WorkoutPlan;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
  boolean existsByWorkoutIdAndPlanId(int workoutId, int planId);
  Optional<WorkoutPlan> findByWorkoutIdAndPlanId(int workoutId, int planId);

  List<WorkoutPlan> findByPlanId(int planId);
}