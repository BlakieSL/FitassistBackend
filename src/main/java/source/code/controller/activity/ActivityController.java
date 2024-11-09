package source.code.controller.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityAverageMetResponseDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.service.declaration.activity.ActivityService;

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
    public ResponseEntity<List<ActivityResponseDto>> getActivitiesByCategory(
            @PathVariable int categoryId) {
        return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ActivityResponseDto>> getFilteredActivities(
            @Valid @RequestBody FilterDto filterDto) {
        List<ActivityResponseDto> filteredActivities = activityService.getFilteredActivities(filterDto);
        return ResponseEntity.ok(filteredActivities);
    }

    @GetMapping("/met")
    public ResponseEntity<ActivityAverageMetResponseDto> getAverageMet() {
        ActivityAverageMetResponseDto dto = activityService.getAverageMet();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDto> createActivity(
            @Valid @RequestBody ActivityCreateDto dto)
    {
        ActivityResponseDto response = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateActivity(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
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
            @Valid @RequestBody CalculateActivityCaloriesRequestDto request
    ) {
        ActivityCalculatedResponseDto response = activityService.calculateCaloriesBurned(id, request);
        return ResponseEntity.ok(response);
    }
}
