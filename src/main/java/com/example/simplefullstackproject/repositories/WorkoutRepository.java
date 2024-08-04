package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
}