package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Plan.Plan;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
  List<Plan> findByPlanType_Id(int planTypeId);

  List<Plan> findByPlanDuration_Id(int planDurationId);

  List<Plan> findByPlanEquipment_Id(int planEquipmentId);

  List<Plan> findByPlanExpertiseLevel_Id(int planExpertiseLevelId);
}