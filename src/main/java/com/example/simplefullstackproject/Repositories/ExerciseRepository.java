package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
}