package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Integer> {
}