package source.code.service.declaration.plan;

import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;

import java.util.List;

public interface PlanPopulationService {

	void populate(List<PlanSummaryDto> plans);

	void populate(PlanResponseDto dto);

}
