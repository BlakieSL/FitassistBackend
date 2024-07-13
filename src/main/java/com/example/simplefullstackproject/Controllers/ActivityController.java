package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Dtos.CalculateCaloriesBurntRequest;
import com.example.simplefullstackproject.Services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activities")
    public ResponseEntity<?> getActivities() {
        return ResponseEntity.ok(activityService.getActivities());
    }

    @GetMapping("/activityCategories")
    public ResponseEntity<?> getActivityCategories() {
        return ResponseEntity.ok(activityService.getCategories());
    }

    @GetMapping("/activities/{categoryId}")
    public ResponseEntity<?> getActivitiesByCategory(@PathVariable int categoryId) {
        return ResponseEntity.ok(activityService.getActivitiesByCategory(categoryId));
    }

    @GetMapping("/activity/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable int id) {
        try {
            ActivityDto activity = activityService.getActivityById(id);
            return ResponseEntity.ok(activity);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/activity/{id}")
    public ResponseEntity<?> calculateCaloriesBurnt(
            @PathVariable int id,
            @Valid @RequestBody CalculateCaloriesBurntRequest request,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }
            ActivityDtoResponse response = activityService.calculateCaloriesBurnt(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/activities/add")
    public ResponseEntity<?> saveActivity(
            @Valid @RequestBody ActivityDto activityDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }
            ActivityDto response = activityService.saveActivity(activityDto);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
