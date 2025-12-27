package source.code.service.declaration.plan;

import java.util.List;

import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;

public interface PlanPopulationService {

	void populate(List<PlanSummaryDto> plans);

	void populate(PlanResponseDto dto);

}
