package source.code.controller.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.DailyCartOwner;
import source.code.dto.request.activity.DailyActivitiesGetDto;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.daily.DailyActivitiesResponseDto;
import source.code.service.declaration.daily.DailyActivityService;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @PostMapping
    public ResponseEntity<DailyActivitiesResponseDto> getAllDailyActivitiesByUserAndDate(
            @Valid @RequestBody DailyActivitiesGetDto request) {
        return ResponseEntity.ok(dailyActivityService.getActivitiesFromDailyCart(request));
    }

    // This endpoint doesn't need to be annotated with @DailyCartOwner since under the hood item is
    // added to user retrieve from the auth context
    @PostMapping("/add/{activityId}")
    public ResponseEntity<Void> addDailyActivityToUser(
            @PathVariable int activityId,
            @Valid @RequestBody DailyActivityItemCreateDto request
    ) {
        dailyActivityService.addActivityToDailyCart(activityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DailyCartOwner
    @PatchMapping("/modify-activity/{dailyActivityItemId}")
    public ResponseEntity<Void> updateDailyCartActivity(
            @PathVariable int dailyActivityItemId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        dailyActivityService.updateDailyActivityItem(dailyActivityItemId, patch);
        return ResponseEntity.noContent().build();
    }

    @DailyCartOwner
    @DeleteMapping("/remove/{dailyActivityItemId}")
    public ResponseEntity<Void> removeActivityFromDailyCartActivity(@PathVariable int dailyActivityItemId) {
        dailyActivityService.removeActivityFromDailyCart(dailyActivityItemId);
        return ResponseEntity.noContent().build();
    }
}
