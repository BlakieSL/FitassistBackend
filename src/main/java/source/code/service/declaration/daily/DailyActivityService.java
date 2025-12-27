package source.code.service.declaration.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.activity.DailyActivitiesGetDto;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.daily.DailyActivitiesResponseDto;

public interface DailyActivityService {

	void addActivityToDailyCart(int activityId, DailyActivityItemCreateDto dto);

	void removeActivityFromDailyCart(int dailyActivityItemId);

	void updateDailyActivityItem(int dailyActivityItemId, JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException;

	DailyActivitiesResponseDto getActivitiesFromDailyCart(DailyActivitiesGetDto request);

}
