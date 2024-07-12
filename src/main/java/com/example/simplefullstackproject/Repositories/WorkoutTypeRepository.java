package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {
}