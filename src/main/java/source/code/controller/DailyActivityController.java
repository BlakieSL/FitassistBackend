package source.code.controller;

import source.code.dto.DailyActivitiesResponse;
import source.code.dto.DailyActivityDto;
import source.code.exception.ValidationException;
import source.code.service.DailyActivityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DailyActivitiesResponse> getAllDailyActivitiesByUserId(@PathVariable int userId) {
        DailyActivitiesResponse activities = dailyActivityService.getActivitiesInCart(userId);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{userId}/add/{activityId}")
    public ResponseEntity<Void> addDailyActivitiesByUserId(
            @PathVariable int userId,
            @PathVariable int activityId,
            @Valid @RequestBody DailyActivityDto request) {

        dailyActivityService.addActivityToDailyActivities(userId, activityId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{activityId}")
    public ResponseEntity<Void> removeDailyActivity(@PathVariable int userId, @PathVariable int activityId) {
        dailyActivityService.removeActivityFromCart(userId, activityId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-activity/{activityId}")
    public ResponseEntity<Void> modifyDailyCartActivity(
            @PathVariable int userId,
            @PathVariable int activityId,
            @Valid @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {

        dailyActivityService.modifyDailyCartActivities(userId, activityId, patch);
        return ResponseEntity.noContent().build();
    }
}
