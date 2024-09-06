package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.PlanCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {
    List<PlanCategoryAssociation> findByPlanCategoryId(Integer categoryId);
}