package com.fitassist.backend.repository;

import com.fitassist.backend.model.workout.WorkoutSetExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSetExerciseRepository extends JpaRepository<WorkoutSetExercise, Integer> {

	List<WorkoutSetExercise> findAllByWorkoutSetId(Integer workoutSetId);

}
