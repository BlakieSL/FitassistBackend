package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.workout.WorkoutSetExercise;

import java.util.List;

public interface WorkoutSetExerciseRepository extends JpaRepository<WorkoutSetExercise, Integer> {

	List<WorkoutSetExercise> findAllByWorkoutSetId(Integer workoutSetId);

}
