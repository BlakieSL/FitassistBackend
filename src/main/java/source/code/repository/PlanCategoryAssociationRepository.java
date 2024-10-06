package source.code.repository;

import source.code.model.PlanCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCategoryAssociationRepository extends JpaRepository<PlanCategoryAssociation, Integer> {
    List<PlanCategoryAssociation> findByPlanCategoryId(int categoryId);
}