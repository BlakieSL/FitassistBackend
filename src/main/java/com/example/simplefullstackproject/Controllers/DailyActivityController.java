package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Dtos.DailyActivityDto;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import com.example.simplefullstackproject.Services.DailyActivityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api/daily-activities")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;

    public DailyActivityController(DailyActivityService dailyActivityService) {
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ActivityDtoResponse>> getAllDailyActivitiesByUserId(@PathVariable int userId) {
        List<ActivityDtoResponse> activities = dailyActivityService.getActivitiesInCart(userId);
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
