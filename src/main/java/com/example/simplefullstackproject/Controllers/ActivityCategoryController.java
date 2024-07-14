package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Services.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/activity-categories")
public class ActivityCategoryController {
    private final ActivityService activityService;

    public ActivityCategoryController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<?> geAlActivityCategories() {
        return ResponseEntity.ok(activityService.getCategories());
    }

    @GetMapping("/{categoryId}/activities")
    public ResponseEntity<?> getActivitiesByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
    }
}