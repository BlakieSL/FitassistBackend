package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.PlanCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanCategoryRepository extends JpaRepository<PlanCategory, Integer> {
}