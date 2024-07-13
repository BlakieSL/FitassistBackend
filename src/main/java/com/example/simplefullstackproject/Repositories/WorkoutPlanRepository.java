package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
}