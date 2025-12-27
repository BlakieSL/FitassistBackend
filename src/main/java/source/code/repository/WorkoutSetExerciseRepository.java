package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.workout.WorkoutSetExercise;

public interface WorkoutSetExerciseRepository extends JpaRepository<WorkoutSetExercise, Integer> {

	List<WorkoutSetExercise> findAllByWorkoutSetId(Integer workoutSetId);

}
