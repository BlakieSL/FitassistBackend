package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.helper.CalculationsHelper;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.ActivityMapper;
import com.example.simplefullstackproject.model.Activity;
import com.example.simplefullstackproject.model.ActivityCategory;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.model.UserActivity;
import com.example.simplefullstackproject.repository.ActivityCategoryRepository;
import com.example.simplefullstackproject.repository.ActivityRepository;
import com.example.simplefullstackproject.repository.UserActivityRepository;
import com.example.simplefullstackproject.repository.UserRepository;
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
            final ValidationHelper validationHelper,
            final CalculationsHelper calculationsHelper,
            final ActivityMapper activityMapper,
            final ActivityRepository activityRepository,
            final UserRepository userRepository,
            final ActivityCategoryRepository activityCategoryRepository,
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
    public ActivitySummaryDto saveActivity(final ActivityAdditionDto dto) {
        validationHelper.validate(dto);
        Activity activity = activityRepository.save(activityMapper.toEntity(dto));
        return activityMapper.toSummaryDto(activity);
    }

    public ActivitySummaryDto getActivityById(final Integer id) {
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

    public List<ActivitySummaryDto> getActivitiesByCategory(final Integer categoryId) {
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

    public List<ActivitySummaryDto> getActivitiesByUserID(Integer userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        List<Activity> activities = userActivities.stream()
                .map(UserActivity::getActivity)
                .collect(Collectors.toList());
        return activities.stream()
                .map(activityMapper::toSummaryDto)
                .collect(Collectors.toList());
    }
}