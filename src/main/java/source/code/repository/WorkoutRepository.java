package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.workout.Workout;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {

	@Query("""
			    SELECT w
			    FROM Workout w
			    LEFT JOIN FETCH w.workoutSets ws
			    LEFT JOIN FETCH ws.workoutSetExercises wse
			    LEFT JOIN FETCH wse.exercise e
			    WHERE w.plan.id = :planId
			    ORDER BY w.orderIndex ASC
			""")
	List<Workout> findAllByPlanId(@Param("planId") int planId);

	@Query("""
			    SELECT w
			    FROM Workout w
			    LEFT JOIN FETCH w.workoutSets ws
			    LEFT JOIN FETCH ws.workoutSetExercises wse
			    LEFT JOIN FETCH wse.exercise e
			    WHERE w.id = :workoutId
			""")
	Optional<Workout> findByIdWithDetails(@Param("workoutId") int workoutId);

}
