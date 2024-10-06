package source.code.controller;

import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivitySummaryResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
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
    public ResponseEntity<ActivitySummaryResponseDto> getActivity(@PathVariable int id) {
        ActivitySummaryResponseDto activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity<List<ActivitySummaryResponseDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getActivities());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivitySummaryResponseDto>> getActivitiesByUser(@PathVariable int userId) {
        List<ActivitySummaryResponseDto> recipes = activityService.getActivitiesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> getActivityLikesAndSaves(@PathVariable int id) {
        LikesAndSavesResponseDto dto = userActivityService.calculateLikesAndSavesByActivityId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/met")
    public ResponseEntity<ActivityAverageMetResponseDto> getAverageMet(){
        ActivityAverageMetResponseDto dto = activityService.getAverageMet();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/calculate-calories")
    public ResponseEntity<ActivityCalculatedResponseDto> calculateActivityCaloriesBurned(@PathVariable int id, @Valid @RequestBody CalculateActivityCaloriesRequestDto request) {
        ActivityCalculatedResponseDto response = activityService.calculateCaloriesBurnt(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActivitySummaryResponseDto> createActivity(@Valid @RequestBody ActivityCreateDto dto) {
        ActivitySummaryResponseDto response = activityService.saveActivity(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ActivitySummaryResponseDto>> searchActivities(@Valid @RequestBody SearchRequestDto request){
        return ResponseEntity.ok(activityService.searchActivities(request));
    }
}
