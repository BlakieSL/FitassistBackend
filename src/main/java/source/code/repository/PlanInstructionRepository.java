package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.text.PlanInstruction;

public interface PlanInstructionRepository extends JpaRepository<PlanInstruction, Integer> {

	List<PlanInstruction> getAllByPlanId(int planId);

}
