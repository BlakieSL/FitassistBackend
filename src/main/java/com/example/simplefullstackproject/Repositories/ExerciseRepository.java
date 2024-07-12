package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    @Query("SELECT exercise " +
            "FROM Exercise exercise " +
            "JOIN exercise.users users " +
            "WHERE users.id = :userId")
    List<Exercise> findByUserId(Integer useId);
}