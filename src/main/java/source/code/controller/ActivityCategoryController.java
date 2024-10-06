package source.code.controller;

import source.code.dto.ActivityCategoryDto;
import source.code.dto.ActivitySummaryDto;
import source.code.service.ActivityService;
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