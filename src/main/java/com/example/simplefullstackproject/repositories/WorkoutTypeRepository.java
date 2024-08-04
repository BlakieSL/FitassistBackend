package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {
}