package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Plan.PlanCategoryAssociation;

import java.util.List;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {
  List<PlanCategoryAssociation> findByPlanCategoryId(int categoryId);
}