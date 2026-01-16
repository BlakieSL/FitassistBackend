package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.plan.PlanCategoryAssociation;

import java.util.List;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {

	List<PlanCategoryAssociation> findByPlanCategoryId(int categoryId);

}
