package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByNameContainingIgnoreCase(String name);
}