package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Plan.PlanEquipment;

public interface PlanEquipmentRepository extends JpaRepository<PlanEquipment, Integer> {
}