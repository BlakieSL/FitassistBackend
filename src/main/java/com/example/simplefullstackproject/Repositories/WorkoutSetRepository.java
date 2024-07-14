package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
    List<WorkoutSet> findByWorkoutTypeId(Integer  workoutTypeId);
}