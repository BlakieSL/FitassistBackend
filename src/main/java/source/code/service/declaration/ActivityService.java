package source.code.service.declaration;

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

  ActivityResponseDto getActivity(int id);

  List<ActivityResponseDto> getAllActivities();

  List<ActivityCategoryResponseDto> getAllCategories();

  List<ActivityResponseDto> getActivitiesByCategory(int categoryId);

  ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

  List<ActivityResponseDto> searchActivities(SearchRequestDto request);

  List<ActivityResponseDto> getActivitiesByUser(int userId);

  ActivityAverageMetResponseDto getAverageMet();
}
