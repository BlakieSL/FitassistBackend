package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
}