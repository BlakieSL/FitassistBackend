package source.code.service.declaration.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;

public interface DailyActivityService {
    void addActivityToDailyActivityItem(int activityId, DailyActivityItemCreateDto dto);

    void removeActivityFromDailyActivity(int activityId);

    void updateDailyActivityItem(int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    DailyActivitiesResponseDto getActivitiesFromDailyActivity();
}
