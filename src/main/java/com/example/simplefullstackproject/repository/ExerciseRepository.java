package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByNameContainingIgnoreCase(String name);
}