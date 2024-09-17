package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
    List<WorkoutSet> findByWorkoutTypeId(int  workoutTypeId);
}