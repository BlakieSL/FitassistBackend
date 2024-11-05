package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.plan.PlanDuration;

public interface PlanDurationRepository extends JpaRepository<PlanDuration, Integer> {
}