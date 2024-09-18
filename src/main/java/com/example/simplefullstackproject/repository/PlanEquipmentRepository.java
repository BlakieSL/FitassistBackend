package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.PlanEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanEquipmentRepository extends JpaRepository<PlanEquipment, Integer> {
}