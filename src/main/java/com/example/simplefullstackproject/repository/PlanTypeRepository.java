package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanTypeRepository extends JpaRepository<PlanType, Integer> {
}