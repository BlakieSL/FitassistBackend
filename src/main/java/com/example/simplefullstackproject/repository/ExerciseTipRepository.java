package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseTip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {
    List<ExerciseTip> getAllByExerciseId(int exerciseId);
}