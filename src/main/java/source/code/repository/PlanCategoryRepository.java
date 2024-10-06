package source.code.repository;

import source.code.model.PlanCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanCategoryRepository extends JpaRepository<PlanCategory, Integer> {
}