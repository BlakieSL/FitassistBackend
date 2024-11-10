package source.code.controller.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.service.declaration.daily.DailyActivityService;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping
    public ResponseEntity<DailyActivitiesResponseDto> getAllDailyActivitiesByUser() {
        return ResponseEntity.ok(dailyActivityService.getActivitiesFromDailyActivity());
    }

    @PostMapping("/add/{activityId}")
    public ResponseEntity<Void> addDailyActivityToUser(
            @PathVariable int activityId,
            @Valid @RequestBody DailyActivityItemCreateDto request
    ) {
        dailyActivityService.addActivityToDailyActivityItem(activityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/modify-activity/{activityId}")
    public ResponseEntity<Void> updateDailyCartActivity(
            @PathVariable int activityId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        dailyActivityService.updateDailyActivityItem(activityId, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/{activityId}")
    public ResponseEntity<Void> removeActivityFromDailyCartActivity(@PathVariable int activityId) {
        dailyActivityService.removeActivityFromDailyActivity(activityId);
        return ResponseEntity.noContent().build();
    }
}
