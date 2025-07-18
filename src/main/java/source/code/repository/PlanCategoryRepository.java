package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.plan.PlanCategory;

public interface PlanCategoryRepository extends JpaRepository<PlanCategory, Integer> {
    boolean existsByIdAndPlanCategoryAssociationsIsNotEmpty(Integer id);
}