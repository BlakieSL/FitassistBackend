package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.plan.PlanType;

public interface PlanTypeRepository extends JpaRepository<PlanType, Integer> {
}