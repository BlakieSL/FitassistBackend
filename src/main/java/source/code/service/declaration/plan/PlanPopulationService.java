package source.code.service.declaration.plan;

import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.user.TypeOfInteraction;

import java.util.List;

public interface PlanPopulationService {
    void populate(List<PlanSummaryDto> plans);
}