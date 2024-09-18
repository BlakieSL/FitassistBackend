package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {
}