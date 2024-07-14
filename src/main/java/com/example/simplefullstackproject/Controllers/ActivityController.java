package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Dtos.CalculateCaloriesBurntRequest;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import com.example.simplefullstackproject.Services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
}
