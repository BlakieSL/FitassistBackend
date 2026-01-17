package com.fitassist.backend.repository;

import com.fitassist.backend.model.workout.WorkoutSet;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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
