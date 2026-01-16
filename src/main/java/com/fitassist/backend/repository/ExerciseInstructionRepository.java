package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.text.ExerciseInstruction;

import java.util.List;

public interface ExerciseInstructionRepository extends JpaRepository<ExerciseInstruction, Integer> {

	List<ExerciseInstruction> getAllByExerciseId(int exerciseId);

}
