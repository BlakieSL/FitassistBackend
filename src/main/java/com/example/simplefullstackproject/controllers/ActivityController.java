package com.example.simplefullstackproject.controllers;

import com.example.simplefullstackproject.dtos.*;
import com.example.simplefullstackproject.exceptions.ValidationException;
import com.example.simplefullstackproject.services.ActivityService;
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
    public ResponseEntity<List<ActivityDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getActivityById(@PathVariable int id) {
        ActivityDto activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @PostMapping("/{id}/calculate-calories")
    public ResponseEntity<ActivityDtoResponse> calculateCaloriesBurntById(
            @PathVariable int id,
            @Valid @RequestBody CalculateCaloriesBurntRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ActivityDtoResponse response = activityService.calculateCaloriesBurnt(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActivityDto> createActivity(
            @Valid @RequestBody ActivityDto activityDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ActivityDto response = activityService.saveActivity(activityDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ActivityDto>> searchActivities(
            @Valid @RequestBody SearchDtoRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(activityService.searchActivities(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityDto>> getRecipesByUserId(@PathVariable Integer userId) {
        List<ActivityDto> recipes = activityService.getActivitiesByUserID(userId);
        return ResponseEntity.ok(recipes);
    }
}
