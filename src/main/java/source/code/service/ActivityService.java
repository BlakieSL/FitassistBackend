package source.code.service;

import source.code.dto.*;
import source.code.helper.CalculationsHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.ActivityMapper;
import source.code.model.Activity;
import source.code.model.ActivityCategory;
import source.code.model.User;
import source.code.model.UserActivity;
import source.code.repository.ActivityCategoryRepository;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ActivityMapper activityMapper;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final UserActivityRepository userActivityRepository;
    public ActivityService(
            ValidationHelper validationHelper,
            CalculationsHelper calculationsHelper,
            ActivityMapper activityMapper,
            ActivityRepository activityRepository,
            UserRepository userRepository,
            ActivityCategoryRepository activityCategoryRepository,
            UserActivityRepository userActivityRepository) {
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.activityMapper = activityMapper;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.activityCategoryRepository = activityCategoryRepository;
        this.userActivityRepository = userActivityRepository;
    }

    @Transactional
    public ActivitySummaryDto saveActivity(ActivityAdditionDto dto) {
        validationHelper.validate(dto);
        Activity activity = activityRepository.save(activityMapper.toEntity(dto));
        return activityMapper.toSummaryDto(activity);
    }

    public ActivitySummaryDto getActivityById(int id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + id + " not found"));
        return activityMapper.toSummaryDto(activity);
    }

    public List<ActivitySummaryDto> getActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activityMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<ActivityCategoryDto> getCategories() {
        List<ActivityCategory> categories = activityCategoryRepository.findAll();
        return categories.stream()
                .map(activityMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<ActivitySummaryDto> getActivitiesByCategory(int categoryId) {
        List<Activity> activities = activityRepository
                .findAllByActivityCategory_Id(categoryId);
        return activities.stream()
                .map(activityMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public ActivityCalculatedDto calculateCaloriesBurnt(
            final int id, final CalculateCaloriesBurntRequest request) {
        validationHelper.validate(request);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + request.getUserId() + " not found"));
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + id + " not found"));
        ActivityCalculatedDto response = activityMapper.toCalculatedDto(activity, user, request.getTime());
        return response;
    }

    public List<ActivitySummaryDto> searchActivities(SearchDtoRequest request){
        List<Activity> activities = activityRepository.findAllByNameContainingIgnoreCase(request.getName());
        return activities.stream()
                .map(activityMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<ActivitySummaryDto> getActivitiesByUserID(int userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        List<Activity> activities = userActivities.stream()
                .map(UserActivity::getActivity)
                .collect(Collectors.toList());
        return activities.stream()
                .map(activityMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public ActivityMetDto getAverageMet() {
        List<Activity> activities = activityRepository.findAll();

        double averageMet = activities.stream()
                .mapToDouble(Activity::getMet)
                .average()
                .orElse(0.0);

        return new ActivityMetDto(averageMet);
    }
}