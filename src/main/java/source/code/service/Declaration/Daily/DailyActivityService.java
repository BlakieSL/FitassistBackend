package source.code.service.Declaration.Daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Activity.DailyActivityItemCreateDto;
import source.code.dto.Response.DailyActivitiesResponseDto;

public interface DailyActivityService {
  void addActivityToDailyActivityItem(int userId, Integer activityId, DailyActivityItemCreateDto dto);

  void removeActivityFromDailyActivity(int userId, int activityId);

  void updateDailyActivityItem(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  DailyActivitiesResponseDto getActivitiesFromDailyActivity(int userId);
}
