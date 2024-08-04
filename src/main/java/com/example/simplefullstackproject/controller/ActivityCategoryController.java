package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.ActivityCategoryDto;
import com.example.simplefullstackproject.dto.ActivitySummaryDto;
import com.example.simplefullstackproject.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activity-categories")
public class ActivityCategoryController {
    private final ActivityService activityService;

    public ActivityCategoryController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityCategoryDto>> geAlActivityCategories() {
        return ResponseEntity.ok(activityService.getCategories());
    }

    @GetMapping("/{categoryId}/activities")
    public ResponseEntity<List<ActivitySummaryDto>> getActivitiesByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
    }
}