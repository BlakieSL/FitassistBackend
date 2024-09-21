package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseInstructionRepository extends JpaRepository<ExerciseInstruction, Integer> {
    List<ExerciseInstruction> getAllByExerciseId(int exerciseId);
}