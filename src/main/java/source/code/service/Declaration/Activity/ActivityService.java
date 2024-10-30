package source.code.service.Declaration.Activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Activity.ActivityCreateDto;
import source.code.dto.Request.Activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Response.ActivityAverageMetResponseDto;
import source.code.dto.Response.ActivityCalculatedResponseDto;
import source.code.dto.Response.ActivityResponseDto;
import source.code.model.Activity.Activity;

import java.util.List;


public interface ActivityService {
  ActivityResponseDto createActivity(ActivityCreateDto dto);

  void updateActivity(int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException;

  void deleteActivity(int activityId);

  ActivityResponseDto getActivity(int id);

  List<ActivityResponseDto> getAllActivities();
  List<ActivityResponseDto> getFilteredActivities(FilterDto filterDto);

  List<Activity> getAllActivityEntities();

  List<ActivityResponseDto> getActivitiesByCategory(int categoryId);

  ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

  ActivityAverageMetResponseDto getAverageMet();
}
