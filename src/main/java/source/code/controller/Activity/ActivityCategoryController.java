package source.code.controller.Activity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.service.declaration.ActivityCategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activity-categories")
public class ActivityCategoryController {
  private final ActivityCategoryService activityCategoryService;

  public ActivityCategoryController(ActivityCategoryService activityCategoryService) {
    this.activityCategoryService = activityCategoryService;
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponseDto>> getAllActivityCategories() {
    return ResponseEntity.ok(activityCategoryService.getAllActivityCategories());
  }
}