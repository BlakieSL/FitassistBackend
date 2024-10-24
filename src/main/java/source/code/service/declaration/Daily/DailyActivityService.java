package source.code.service.declaration.Daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Activity.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;

public interface DailyActivityService {
  void addActivityToDailyActivityItem(int userId, Integer activityId, DailyActivityItemCreateDto dto);

  void removeActivityFromDailyActivity(int userId, int activityId);

  void updateDailyActivityItem(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  DailyActivitiesResponseDto getActivitiesFromDailyActivity(int userId);
}
