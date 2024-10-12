package source.code.controller.Activity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.service.declaration.ActivityService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activity-categories")
public class ActivityCategoryController {
  private final ActivityService activityService;

  public ActivityCategoryController(ActivityService activityService) {
    this.activityService = activityService;
  }

  @GetMapping
  public ResponseEntity<List<ActivityCategoryResponseDto>> getAllActivityCategories() {
    return ResponseEntity.ok(activityService.getAllCategories());
  }

  @GetMapping("/{categoryId}/activities")
  public ResponseEntity<List<ActivityResponseDto>> getActivitiesByCategory(@PathVariable int categoryId) {
    return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
  }
}