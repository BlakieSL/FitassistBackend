package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
}