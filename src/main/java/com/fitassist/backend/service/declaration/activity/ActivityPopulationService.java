package com.fitassist.backend.service.declaration.activity;

import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;

import java.util.List;

public interface ActivityPopulationService {

	void populate(ActivityResponseDto activity);

	void populate(List<ActivitySummaryDto> activities);

}
