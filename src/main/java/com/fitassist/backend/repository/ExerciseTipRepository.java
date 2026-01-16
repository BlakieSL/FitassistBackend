package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.text.ExerciseTip;

import java.util.List;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {

	List<ExerciseTip> getAllByExerciseId(int exerciseId);

}
