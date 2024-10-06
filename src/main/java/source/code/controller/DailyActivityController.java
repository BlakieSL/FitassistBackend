package source.code.controller;

import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.service.DailyActivityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DailyActivitiesResponseDto> getAllDailyActivitiesByUser(@PathVariable int userId) {
        DailyActivitiesResponseDto activities = dailyActivityService.getActivitiesInCart(userId);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{userId}/add/{activityId}")
    public ResponseEntity<Void> addDailyActivityToUser(
            @PathVariable int userId,
            @PathVariable int activityId,
            @Valid @RequestBody DailyCartActivityCreateDto request) {

        dailyActivityService.addActivityToDailyActivities(userId, activityId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{activityId}")
    public ResponseEntity<Void> removeActivityFromDailyCartActivity(@PathVariable int userId, @PathVariable int activityId) {
        dailyActivityService.removeActivityFromCart(userId, activityId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-activity/{activityId}")
    public ResponseEntity<Void> updateDailyCartActivity(
            @PathVariable int userId,
            @PathVariable int activityId,
            @Valid @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {

        dailyActivityService.modifyDailyCartActivities(userId, activityId, patch);
        return ResponseEntity.noContent().build();
    }
}
