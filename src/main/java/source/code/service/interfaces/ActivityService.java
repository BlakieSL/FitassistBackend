package source.code.service.interfaces;

import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivitySummaryResponseDto;

import java.util.List;


public interface ActivityService {
    ActivitySummaryResponseDto createActivity(ActivityCreateDto dto);

    ActivitySummaryResponseDto getActivity(int id);

    List<ActivitySummaryResponseDto> getAllActivities();

    List<ActivityCategoryResponseDto> getAllCategories();

    List<ActivitySummaryResponseDto> getActivitiesByCategory(int categoryId);

    ActivityCalculatedResponseDto calculateCaloriesBurned(int id, CalculateActivityCaloriesRequestDto request);

    List<ActivitySummaryResponseDto> searchActivities(SearchRequestDto request);

    List<ActivitySummaryResponseDto> getActivitiesByUser(int userId);

    ActivityAverageMetResponseDto getAverageMet();
}
