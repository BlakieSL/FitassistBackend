package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.ActivityCalculatedDto;
import com.example.simplefullstackproject.dto.ActivityCategoryDto;
import com.example.simplefullstackproject.dto.ActivitySummaryDto;
import com.example.simplefullstackproject.model.Activity;
import com.example.simplefullstackproject.model.ActivityCategory;
import com.example.simplefullstackproject.repository.ActivityCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ActivityDtoMapper {
    private final ActivityCategoryRepository activityCategoryRepository;

    public ActivityDtoMapper(ActivityCategoryRepository activityCategoryRepository) {
        this.activityCategoryRepository = activityCategoryRepository;
    }

    public ActivitySummaryDto map(Activity activity) {
        return new ActivitySummaryDto(
                activity.getId(),
                activity.getName(),
                activity.getMet(),
                activity.getActivityCategory().getName());
    }

    public ActivityCalculatedDto mapCalculated(Activity activity) {
        ActivityCalculatedDto response = new ActivityCalculatedDto();
        response.setId(activity.getId());
        response.setName(activity.getName());
        response.setMet(activity.getMet());
        response.setCategoryName(activity.getActivityCategory().getName());
        return response;
    }

    public Activity map(ActivitySummaryDto request) {
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
