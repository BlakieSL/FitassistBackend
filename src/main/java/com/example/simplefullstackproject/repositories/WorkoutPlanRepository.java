package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
    List<WorkoutPlan> findByPlanId(int planId);
    Optional<WorkoutPlan> findByPlanIdAndWorkoutId(Integer planId, Integer workoutId);
}