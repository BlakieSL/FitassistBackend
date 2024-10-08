package source.code.controller;

import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.service.interfaces.ActivityService;
import source.code.service.interfaces.UserActivityService;

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
    public ResponseEntity<ActivityResponseDto> getActivity(@PathVariable int id) {
        ActivityResponseDto activity =  activityService.getActivity(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponseDto>> getActivitiesByUser(@PathVariable int userId) {
        List<ActivityResponseDto> recipes =  activityService.getActivitiesByUser(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> getActivityLikesAndSaves(@PathVariable int id) {
        LikesAndSavesResponseDto dto = userActivityService.calculateActivityLikesAndSaves(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/met")
    public ResponseEntity<ActivityAverageMetResponseDto> getAverageMet(){
        ActivityAverageMetResponseDto dto =  activityService.getAverageMet();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/calculate-calories")
    public ResponseEntity<ActivityCalculatedResponseDto> calculateActivityCaloriesBurned(@PathVariable int id, @Valid @RequestBody CalculateActivityCaloriesRequestDto request) {
        ActivityCalculatedResponseDto response =  activityService.calculateCaloriesBurned(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDto> createActivity(@Valid @RequestBody ActivityCreateDto dto) {
        ActivityResponseDto response =  activityService.createActivity(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ActivityResponseDto>> searchActivities(@Valid @RequestBody SearchRequestDto request){
        return ResponseEntity.ok( activityService.searchActivities(request));
    }
}
