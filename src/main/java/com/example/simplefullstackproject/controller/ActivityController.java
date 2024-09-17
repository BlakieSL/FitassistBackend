package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
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

    @GetMapping("/{id}")
    public ResponseEntity<ActivitySummaryDto> getActivityById(@PathVariable int id) {
        ActivitySummaryDto activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @PostMapping("/{id}/calculate-calories")
    public ResponseEntity<ActivityCalculatedDto> calculateCaloriesBurntById(
            @PathVariable int id,
            @Valid @RequestBody CalculateCaloriesBurntRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ActivityCalculatedDto response = activityService.calculateCaloriesBurnt(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActivitySummaryDto> createActivity(
            @Valid @RequestBody ActivityAdditionDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ActivitySummaryDto response = activityService.saveActivity(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ActivitySummaryDto>> searchActivities(
            @Valid @RequestBody SearchDtoRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(activityService.searchActivities(request));
    }
}
