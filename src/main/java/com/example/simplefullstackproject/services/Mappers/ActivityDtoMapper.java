package com.example.simplefullstackproject.services.Mappers;

import com.example.simplefullstackproject.dtos.ActivityCategoryDto;
import com.example.simplefullstackproject.dtos.ActivityDto;
import com.example.simplefullstackproject.dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.models.Activity;
import com.example.simplefullstackproject.models.ActivityCategory;
import com.example.simplefullstackproject.repositories.ActivityCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ActivityDtoMapper {
    private final ActivityCategoryRepository activityCategoryRepository;

    public ActivityDtoMapper(ActivityCategoryRepository activityCategoryRepository) {
        this.activityCategoryRepository = activityCategoryRepository;
    }

    public ActivityDto map(Activity activity) {
        return new ActivityDto(
                activity.getId(),
                activity.getName(),
                activity.getMet(),
                activity.getActivityCategory().getName());
    }

    public ActivityDtoResponse mapCalculated(Activity activity) {
        ActivityDtoResponse response = new ActivityDtoResponse();
        response.setId(activity.getId());
        response.setName(activity.getName());
        response.setMet(activity.getMet());
        response.setCategoryName(activity.getActivityCategory().getName());
        return response;
    }

    public Activity map(ActivityDto request) {
        Activity activity = new Activity();
        activity.setName(request.getName());
        activity.setMet(request.getMet());
        activity.setActivityCategory(activityCategoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity category with name: " + request.getCategoryName() + " not found")));
        return activity;
    }

    public ActivityCategoryDto map(ActivityCategory request) {
        return new ActivityCategoryDto(request.getId(), request.getName());
    }
}
