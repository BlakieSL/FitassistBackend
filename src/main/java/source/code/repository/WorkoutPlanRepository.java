package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.WorkoutPlan;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
  List<WorkoutPlan> findByPlanId(int planId);

  Optional<WorkoutPlan> findByPlanIdAndWorkoutId(int planId, int workoutId);
}