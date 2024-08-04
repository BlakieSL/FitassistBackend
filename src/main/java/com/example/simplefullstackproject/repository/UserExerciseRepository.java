package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
  List<UserExercise> findByUserId(Integer userId);
  Optional<UserExercise> findByUserIdAndExerciseId(Integer userId, Integer exerciseId);
  boolean existsByUserIdAndExerciseId(Integer userId, Integer exerciseId);
}