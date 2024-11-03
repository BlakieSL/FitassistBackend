package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Workout.WorkoutSet;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
  List<WorkoutSet> getAllByWorkoutId(int workoutId);
}