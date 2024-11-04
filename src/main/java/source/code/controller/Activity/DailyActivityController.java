package source.code.controller.Activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.Activity.DailyActivityItemCreateDto;
import source.code.dto.Response.DailyActivitiesResponseDto;
import source.code.service.Declaration.Daily.DailyActivityService;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DailyActivitiesResponseDto> getAllDailyActivitiesByUser(
            @PathVariable int userId) {
        DailyActivitiesResponseDto activities = dailyActivityService.getActivitiesFromDailyActivity(userId);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{userId}/add/{activityId}")
    public ResponseEntity<Void> addDailyActivityToUser(
            @PathVariable int userId,
            @PathVariable int activityId,
            @Valid @RequestBody DailyActivityItemCreateDto request) {

        dailyActivityService.addActivityToDailyActivityItem(userId, activityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/remove/{activityId}")
    public ResponseEntity<Void> removeActivityFromDailyCartActivity(
            @PathVariable int userId,
            @PathVariable int activityId) {

        dailyActivityService.removeActivityFromDailyActivity(userId, activityId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/modify-activity/{activityId}")
    public ResponseEntity<Void> updateDailyCartActivity(
            @PathVariable int userId,
            @PathVariable int activityId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {

        dailyActivityService.updateDailyActivityItem(userId, activityId, patch);
        return ResponseEntity.noContent().build();
    }
}
