package source.code.service.declaration.activity;

import java.util.List;

import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;

public interface ActivityPopulationService {

	void populate(ActivityResponseDto activity);

	void populate(List<ActivitySummaryDto> activities);

}
