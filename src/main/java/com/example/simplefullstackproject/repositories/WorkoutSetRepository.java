package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
    List<WorkoutSet> findByWorkoutTypeId(Integer  workoutTypeId);
}