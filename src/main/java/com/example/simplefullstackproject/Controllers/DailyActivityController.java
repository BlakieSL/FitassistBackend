package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Dtos.DailyActivityDto;
import com.example.simplefullstackproject.Services.DailyActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api")
public class DailyActivityController {
    private final DailyActivityService dailyActivityService;
    public DailyActivityController(DailyActivityService dailyActivityService){
        this.dailyActivityService = dailyActivityService;
    }

    @GetMapping("dailyActivities/{userId}")
    public ResponseEntity<?> getDailyActivities(@PathVariable int userId){
        try{
            List<ActivityDtoResponse> activities = dailyActivityService.getActivitiesInCart(userId);
            return ResponseEntity.ok(activities);
        } catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/dailyActivities/{userId}/add")
    public ResponseEntity<?> addDailyActivities(@PathVariable int userId,
                                                @Valid @RequestBody DailyActivityDto request,
                                                BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }
            dailyActivityService.addActivityToDailyActivities(userId,request);
            return ResponseEntity.ok().build();
        } catch(NoSuchElementException e){
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/dailyActivities/{userId}/remove/{activityId}")
    public ResponseEntity<?> removeDailyActivity(@PathVariable int userId,
                                                 @PathVariable int activityId){
        try{
            dailyActivityService.removeActivityFromCart(userId, activityId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
