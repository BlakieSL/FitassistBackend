package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
}