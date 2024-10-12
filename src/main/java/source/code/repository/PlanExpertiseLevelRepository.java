package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Plan.PlanExpertiseLevel;

public interface PlanExpertiseLevelRepository extends JpaRepository<PlanExpertiseLevel, Integer> {
}