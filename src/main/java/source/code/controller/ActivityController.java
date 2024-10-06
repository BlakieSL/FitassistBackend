package source.code.controller;

import source.code.dto.*;
import source.code.service.ActivityService;
import source.code.service.UserActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activities")
public class ActivityController {
    private final ActivityService activityService;
    private final UserActivityService userActivityService;
    public ActivityController(ActivityService activityService, UserActivityService userActivityService) {
        this.activityService = activityService;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivitySummaryDto> getActivityById(@PathVariable int id) {
        ActivitySummaryDto activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity<List<ActivitySummaryDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getActivities());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivitySummaryDto>> getActivitiesByUserId(@PathVariable int userId) {
        List<ActivitySummaryDto> recipes = activityService.getActivitiesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavedDto> getLikesAndSavedActivity(@PathVariable int id) {
        LikesAndSavedDto dto = userActivityService.calculateLikesAndSavesByActivityId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/met")
    public ResponseEntity<ActivityMetDto> getAverageMet(){
        ActivityMetDto dto = activityService.getAverageMet();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/calculate-calories")
    public ResponseEntity<ActivityCalculatedDto> calculateCaloriesBurntById(@PathVariable int id, @Valid @RequestBody CalculateCaloriesBurntRequest request) {
        ActivityCalculatedDto response = activityService.calculateCaloriesBurnt(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActivitySummaryDto> createActivity(@Valid @RequestBody ActivityAdditionDto dto) {
        ActivitySummaryDto response = activityService.saveActivity(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ActivitySummaryDto>> searchActivities(@Valid @RequestBody SearchDtoRequest request){
        return ResponseEntity.ok(activityService.searchActivities(request));
    }
}
