package source.code.service.declaration.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.model.activity.Activity;

import java.util.List;


public interface ActivityService {
    ActivitySummaryDto createActivity(ActivityCreateDto dto);

    void updateActivity(int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteActivity(int activityId);

    ActivityResponseDto getActivity(int id);

    Page<ActivitySummaryDto> getAllActivities(Pageable pageable);

    Page<ActivitySummaryDto> getFilteredActivities(FilterDto filter, Pageable pageable);

    List<Activity> getAllActivityEntities();

    ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);
}
