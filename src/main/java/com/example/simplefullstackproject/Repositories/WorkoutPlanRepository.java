package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
    List<WorkoutPlan> findByPlanId(int planId);
    Optional<WorkoutPlan> findByPlanIdAndWorkoutId(Integer planId, Integer workoutId);
}