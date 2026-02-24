package com.fitassist.backend.service.declaration.activity;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.CalculateActivityCaloriesRequestDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.model.activity.Activity;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

	ActivityResponseDto createActivity(ActivityCreateDto dto);

	void updateActivity(int activityId, JsonMergePatch patch) throws JacksonException;

	void deleteActivity(int activityId);

	ActivityResponseDto getActivity(int id);

	Page<ActivitySummaryDto> getFilteredActivities(FilterDto filter, Pageable pageable);

	List<Activity> getAllActivityEntities();

	ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

}
