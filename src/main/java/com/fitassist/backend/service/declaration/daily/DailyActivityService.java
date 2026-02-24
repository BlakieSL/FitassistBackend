package com.fitassist.backend.service.declaration.daily;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.activity.DailyActivityItemCreateDto;
import com.fitassist.backend.dto.response.daily.DailyActivitiesResponseDto;
import jakarta.json.JsonMergePatch;

import java.time.LocalDate;

public interface DailyActivityService {

	void addActivityToDailyCart(int activityId, DailyActivityItemCreateDto dto);

	void removeActivityFromDailyCart(int dailyActivityItemId);

	void updateDailyActivityItem(int dailyActivityItemId, JsonMergePatch patch) throws JacksonException;

	DailyActivitiesResponseDto getActivitiesFromDailyCart(LocalDate date);

}
