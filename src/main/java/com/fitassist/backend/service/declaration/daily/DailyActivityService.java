package com.fitassist.backend.service.declaration.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.fitassist.backend.dto.request.activity.DailyActivityItemCreateDto;
import com.fitassist.backend.dto.response.daily.DailyActivitiesResponseDto;

import java.time.LocalDate;

public interface DailyActivityService {

	void addActivityToDailyCart(int activityId, DailyActivityItemCreateDto dto);

	void removeActivityFromDailyCart(int dailyActivityItemId);

	void updateDailyActivityItem(int dailyActivityItemId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException;

	DailyActivitiesResponseDto getActivitiesFromDailyCart(LocalDate date);

}
