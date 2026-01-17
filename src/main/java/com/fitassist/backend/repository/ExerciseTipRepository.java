package com.fitassist.backend.repository;

import com.fitassist.backend.model.text.ExerciseTip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {

	List<ExerciseTip> getAllByExerciseId(int exerciseId);

}
