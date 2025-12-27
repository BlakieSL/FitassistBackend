package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.workout.WorkoutSet;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {

	@Query("""
		    SELECT ws
		    FROM WorkoutSet ws
		    LEFT JOIN FETCH ws.workoutSetExercises wse
		    LEFT JOIN FETCH wse.exercise e
		    WHERE ws.workout.id = :workoutId
		""")
	List<WorkoutSet> findAllByWorkoutId(@Param("workoutId") Integer workoutId);

	@Query("""
		    SELECT ws
		    FROM WorkoutSet ws
		    LEFT JOIN FETCH ws.workoutSetExercises wse
		    LEFT JOIN FETCH wse.exercise e
		    WHERE ws.id = :id
		""")
	Optional<WorkoutSet> findByIdWithDetails(@Param("id") int id);

}
