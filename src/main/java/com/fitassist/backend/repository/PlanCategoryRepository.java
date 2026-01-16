package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.plan.PlanCategory;

import java.util.List;

public interface PlanCategoryRepository extends JpaRepository<PlanCategory, Integer> {

	boolean existsByIdAndPlanCategoryAssociationsIsNotEmpty(Integer id);

	List<PlanCategory> findAllByIdIn(List<Integer> ids);

}
