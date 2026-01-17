package com.fitassist.backend.repository;

import com.fitassist.backend.model.exercise.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

}
