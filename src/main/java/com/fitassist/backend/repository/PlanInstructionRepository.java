package com.fitassist.backend.repository;

import com.fitassist.backend.model.text.PlanInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanInstructionRepository extends JpaRepository<PlanInstruction, Integer> {

	List<PlanInstruction> getAllByPlanId(int planId);

}
