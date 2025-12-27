package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.plan.PlanCategoryAssociation;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {

	List<PlanCategoryAssociation> findByPlanCategoryId(int categoryId);

}
