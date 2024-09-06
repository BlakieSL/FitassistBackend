package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Integer> {
    Optional<ExerciseCategory> findByName(String name);
}