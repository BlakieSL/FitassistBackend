package com.fitassist.backend.service.declaration.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.CalculateActivityCaloriesRequestDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.model.activity.Activity;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

	ActivityResponseDto createActivity(ActivityCreateDto dto);

	void updateActivity(int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteActivity(int activityId);

	ActivityResponseDto getActivity(int id);

	Page<ActivitySummaryDto> getFilteredActivities(FilterDto filter, Pageable pageable);

	List<Activity> getAllActivityEntities();

	ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

}
