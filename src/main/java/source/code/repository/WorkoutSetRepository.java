package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.workout.WorkoutSet;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
    List<WorkoutSet> findAllByWorkoutSetGroupId(Integer workoutSetGroupId);
}