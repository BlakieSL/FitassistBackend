package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.ActivityCalculatedDto;
import com.example.simplefullstackproject.dto.DailyActivitiesResponse;
import com.example.simplefullstackproject.dto.DailyActivityDto;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.DailyActivityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addDailyActivitiesByUserId(
            @PathVariable int userId,
            @Valid @RequestBody DailyActivityDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        dailyActivityService.addActivityToDailyActivities(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{activityId}")
    public ResponseEntity<Void> removeDailyActivity(
            @PathVariable int userId, @PathVariable int activityId) {
        dailyActivityService.removeActivityFromCart(userId, activityId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/modify-activity/{activityId}")
    public ResponseEntity<Void> modifyDailyCartActivity(
            @PathVariable Integer userId,
            @PathVariable Integer activityId,
            @Valid @RequestBody JsonMergePatch patch,
            BindingResult bindingResult
            ) throws JsonPatchException, JsonProcessingException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }

        dailyActivityService.modifyDailyCartActivities(userId, activityId, patch);
        return ResponseEntity.noContent().build();
    }
}
