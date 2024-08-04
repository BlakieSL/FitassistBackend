package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
}