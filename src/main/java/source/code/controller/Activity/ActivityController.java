package source.code.controller.Activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Activity.ActivityCreateDto;
import source.code.dto.request.Activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.service.declaration.Activity.ActivityService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activities")
public class ActivityController {
  private final ActivityService activityService;

  public ActivityController(ActivityService activityService) {
    this.activityService = activityService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ActivityResponseDto> getActivity(@PathVariable int id) {
    ActivityResponseDto activity = activityService.getActivity(id);
    return ResponseEntity.ok(activity);
  }

  @GetMapping
  public ResponseEntity<List<ActivityResponseDto>> getAllActivities() {
    return ResponseEntity.ok(activityService.getAllActivities());
  }

  @GetMapping("/categories/{categoryId}/")
  public ResponseEntity<List<ActivityResponseDto>> getActivitiesByCategory(@PathVariable int categoryId) {
    return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
  }

  @GetMapping("/met")
  public ResponseEntity<ActivityAverageMetResponseDto> getAverageMet() {
    ActivityAverageMetResponseDto dto = activityService.getAverageMet();
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<ActivityResponseDto> createActivity(
          @Valid @RequestBody ActivityCreateDto dto) {

    ActivityResponseDto response = activityService.createActivity(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateActivity(@PathVariable int id, @RequestBody JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    activityService.updateActivity(id, patch);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteActivity(@PathVariable int id) {
    activityService.deleteActivity(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/calculate-calories")
  public ResponseEntity<ActivityCalculatedResponseDto> calculateActivityCaloriesBurned(
          @PathVariable int id,
          @Valid @RequestBody CalculateActivityCaloriesRequestDto request) {

    ActivityCalculatedResponseDto response = activityService.calculateCaloriesBurned(id, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/search")
  public ResponseEntity<List<ActivityResponseDto>> searchActivities(
          @Valid @RequestBody SearchRequestDto request) {

    return ResponseEntity.ok(activityService.searchActivities(request));
  }
}
