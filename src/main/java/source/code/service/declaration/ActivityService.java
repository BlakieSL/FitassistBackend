package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivityResponseDto;

import java.util.List;


public interface ActivityService {
  ActivityResponseDto createActivity(ActivityCreateDto dto);

  void updateActivity(int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException;

  void deleteActivity(int activityId);

  ActivityResponseDto getActivity(int id);

  List<ActivityResponseDto> getAllActivities();

  List<ActivityCategoryResponseDto> getAllCategories();

  List<ActivityResponseDto> getActivitiesByCategory(int categoryId);

  ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

  List<ActivityResponseDto> searchActivities(SearchRequestDto request);

  List<ActivityResponseDto> getActivitiesByUserAndType(int userId, short type);

  ActivityAverageMetResponseDto getAverageMet();
}
