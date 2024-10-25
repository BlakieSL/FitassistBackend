package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Text.PlanInstruction;

import java.util.List;

public interface PlanInstructionRepository extends JpaRepository<PlanInstruction, Integer> {
  List<PlanInstruction> getAllByPlanId(int planId);
}