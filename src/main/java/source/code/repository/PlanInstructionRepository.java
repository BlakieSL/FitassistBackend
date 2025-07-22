package source.code.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.text.PlanInstruction;

import java.util.List;

public interface PlanInstructionRepository extends JpaRepository<PlanInstruction, Integer> {
    List<PlanInstruction> getAllByPlanId(int planId);
}