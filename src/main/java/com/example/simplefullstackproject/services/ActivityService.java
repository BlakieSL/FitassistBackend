package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.*;
import com.example.simplefullstackproject.models.Activity;
import com.example.simplefullstackproject.models.ActivityCategory;
import com.example.simplefullstackproject.models.User;
import com.example.simplefullstackproject.models.UserActivity;
import com.example.simplefullstackproject.repositories.ActivityCategoryRepository;
import com.example.simplefullstackproject.repositories.ActivityRepository;
import com.example.simplefullstackproject.repositories.UserActivityRepository;
import com.example.simplefullstackproject.repositories.UserRepository;
import com.example.simplefullstackproject.services.Mappers.ActivityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ActivityDtoMapper activityDtoMapper;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final UserActivityRepository userActivityRepository;
    public ActivityService(
            final ValidationHelper validationHelper,
            final CalculationsHelper calculationsHelper,
            final ActivityDtoMapper activityDtoMapper,
            final ActivityRepository activityRepository,
            final UserRepository userRepository,
            final ActivityCategoryRepository activityCategoryRepository,
            UserActivityRepository userActivityRepository) {
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.activityDtoMapper = activityDtoMapper;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.activityCategoryRepository = activityCategoryRepository;
        this.userActivityRepository = userActivityRepository;
    }

    @Transactional
    public ActivityDto saveActivity(final ActivityDto activityDto) {
        validationHelper.validate(activityDto);
        Activity activity = activityRepository.save(activityDtoMapper.map(activityDto));
        return activityDtoMapper.map(activity);
    }

    public ActivityDto getActivityById(final Integer id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + id + " not found"));
        return activityDtoMapper.map(activity);
    }

    public List<ActivityDto> getActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<ActivityCategoryDto> getCategories() {
        List<ActivityCategory> categories = activityCategoryRepository.findAll();
        return categories.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<ActivityDto> getActivitiesByCategory(final Integer categoryId) {
        List<Activity> activities = activityRepository
                .findAllByActivityCategory_Id(categoryId);
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public ActivityDtoResponse calculateCaloriesBurnt(
            final int id, final CalculateCaloriesBurntRequest request) {
        validationHelper.validate(request);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + request.getUserId() + " not found"));
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + id + " not found"));
        ActivityDtoResponse response = activityDtoMapper.mapCalculated(activity);
        response.setTime(request.getTime());
        response.setCaloriesBurned((int) calculationsHelper.calculateCaloriesBurned(
                response.getTime(), user.getWeight(), activity.getMet()));
        return response;
    }

    public List<ActivityDto> searchActivities(SearchDtoRequest request){
        List<Activity> activities = activityRepository.findAllByNameContainingIgnoreCase(request.getName());
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<ActivityDto> getActivitiesByUserID(Integer userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        List<Activity> activities = userActivities.stream()
                .map(UserActivity::getActivity)
                .collect(Collectors.toList());
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }
}