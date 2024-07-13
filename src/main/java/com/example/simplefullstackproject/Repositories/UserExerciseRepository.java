package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
  List<UserExercise> findByUserId(Integer userId);
}