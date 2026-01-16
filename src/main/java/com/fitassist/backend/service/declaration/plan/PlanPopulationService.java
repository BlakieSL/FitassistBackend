package com.fitassist.backend.service.declaration.plan;

import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;

import java.util.List;

public interface PlanPopulationService {

	void populate(List<PlanSummaryDto> plans);

	void populate(PlanResponseDto dto);

}
