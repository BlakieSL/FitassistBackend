package source.code.service.declaration.activity;

import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;

import java.util.List;

public interface ActivityPopulationService {
    void populate(ActivityResponseDto activity);

    void populate(List<ActivitySummaryDto> activities);
}
