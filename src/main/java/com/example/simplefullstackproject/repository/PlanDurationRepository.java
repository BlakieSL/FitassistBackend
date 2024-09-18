package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.PlanDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanDurationRepository extends JpaRepository<PlanDuration, Integer> {
}