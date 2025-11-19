package source.code.controller.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.AdminOnly;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.service.declaration.activity.ActivityService;

import java.util.List;


@RestController
@RequestMapping(path = "/api/activities")
public class ActivityController {
    private final ActivityService activityService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityController.class);

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @AdminOnly
    @PostMapping
    public ResponseEntity<ActivitySummaryDto> createActivity(
            @Valid @RequestBody ActivityCreateDto dto)
    {
        ActivitySummaryDto response = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AdminOnly
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateActivity(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        activityService.updateActivity(id, patch);
        return ResponseEntity.noContent().build();
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable int id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivity(@PathVariable int id) {
        ActivityResponseDto activity = activityService.getActivity(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity<List<ActivitySummaryDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ActivitySummaryDto>> getFilteredActivities(
            @Valid @RequestBody FilterDto filterDto) {
        List<ActivitySummaryDto> filteredActivities = activityService.getFilteredActivities(filterDto);
        return ResponseEntity.ok(filteredActivities);
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
