package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.PlanDuration;

public interface PlanDurationRepository extends JpaRepository<PlanDuration, Integer> {
}