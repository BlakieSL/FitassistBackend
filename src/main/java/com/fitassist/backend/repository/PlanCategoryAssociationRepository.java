package com.fitassist.backend.repository;

import com.fitassist.backend.model.plan.PlanCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {

	List<PlanCategoryAssociation> findByPlanCategoryId(int categoryId);

}
