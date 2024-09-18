package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByExpertiseLevel_Id(int expertiseLevelId);
    List<Exercise> findByForceType_Id(int forceTypeId);
    List<Exercise> findByMechanicsType_Id(int mechanicsTypeId);
    List<Exercise> findByExerciseEquipment_Id(int exerciseEquipmentId);
    List<Exercise> findByExerciseType_Id(int exerciseTypeId);
}