package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.model.Activity.DailyActivity;

public interface DailyActivityService {
  void resetDailyCarts();

  void addActivityToDailyActivityItem(int userId, Integer activityId, DailyActivityItemCreateDto dto);

  void removeActivityFromDailyActivity(int userId, int activityId);

  void updateDailyActivityItem(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  DailyActivitiesResponseDto getActivitiesFromDailyActivityItem(int userId);

  DailyActivity createNewDailyActivityForUser(int userId);
}
