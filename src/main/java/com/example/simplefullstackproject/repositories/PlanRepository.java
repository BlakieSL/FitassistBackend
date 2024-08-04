package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
}