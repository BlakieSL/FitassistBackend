package source.code.service.declaration.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.model.activity.Activity;

import java.util.List;


public interface ActivityService {
    ActivityResponseDto createActivity(ActivityCreateDto dto);

    void updateActivity(int activityId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException;

    void deleteActivity(int activityId);

    ActivityResponseDto getActivity(int id);

    List<ActivityResponseDto> getAllActivities();

    List<ActivityResponseDto> getFilteredActivities(FilterDto filter);

    List<Activity> getAllActivityEntities();

    List<ActivityResponseDto> getActivitiesByCategory(int categoryId);

    ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

    ActivityAverageMetResponseDto getAverageMet();
}
