package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseEquipmentRepository extends JpaRepository<ExerciseEquipment, Integer> {
}