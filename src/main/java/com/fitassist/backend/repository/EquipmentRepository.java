package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.exercise.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

}
