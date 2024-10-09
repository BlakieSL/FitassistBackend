package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.model.DailyActivity;

public interface DailyActivityService {
    void updateDailyCarts();
    void addActivityToDailyCartActivity(int userId, Integer activityId, DailyCartActivityCreateDto dto);
    void removeActivityFromDailyActivity(int userId, int activityId);
    void updateDailyCartActivity(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
    DailyActivitiesResponseDto getActivitiesFromDailyCartActivity(int userId);
    DailyActivity createNewDailyActivityForUser(int userId);
}
