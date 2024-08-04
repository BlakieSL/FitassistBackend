package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {
}