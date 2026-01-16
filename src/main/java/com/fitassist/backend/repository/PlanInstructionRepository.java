package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.text.PlanInstruction;

import java.util.List;

public interface PlanInstructionRepository extends JpaRepository<PlanInstruction, Integer> {

	List<PlanInstruction> getAllByPlanId(int planId);

}
