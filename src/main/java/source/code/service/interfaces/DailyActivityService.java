package source.code.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;

public interface DailyActivityService {
    void updateDailyCarts();
    void addActivityToDailyCartActivity(int userId, Integer activityId, DailyCartActivityCreateDto dto);
    void removeActivityFromDailyCartActivity(int userId, int activityId);
    void updateDailyCartActivity(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
    public DailyActivitiesResponseDto getActivitiesFromDailyCartActivity(int userId);
}
