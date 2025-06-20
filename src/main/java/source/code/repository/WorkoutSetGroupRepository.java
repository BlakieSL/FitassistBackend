package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.workout.WorkoutSetGroup;

import java.util.List;

public interface WorkoutSetGroupRepository extends JpaRepository<WorkoutSetGroup, Integer> {
    List<WorkoutSetGroup> findAllByWorkoutId(Integer workoutId);
}
