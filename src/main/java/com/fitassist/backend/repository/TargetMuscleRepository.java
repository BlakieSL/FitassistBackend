package com.fitassist.backend.repository;

import com.fitassist.backend.model.exercise.TargetMuscle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetMuscleRepository extends JpaRepository<TargetMuscle, Integer> {

}
