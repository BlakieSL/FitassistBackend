package com.fitassist.backend.repository;

import com.fitassist.backend.model.exercise.TargetMuscle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetMuscleRepository extends JpaRepository<TargetMuscle, Integer> {

	List<TargetMuscle> findAllByIdIn(List<Integer> ids);

}
