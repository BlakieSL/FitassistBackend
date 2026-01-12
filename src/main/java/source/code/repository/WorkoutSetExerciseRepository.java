package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.workout.WorkoutSetExercise;

import java.util.List;

public interface WorkoutSetExerciseRepository extends JpaRepository<WorkoutSetExercise, Integer> {

	List<WorkoutSetExercise> findAllByWorkoutSetId(Integer workoutSetId);

}
