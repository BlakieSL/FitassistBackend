package com.example.simplefullstackproject.controllers;

import com.example.simplefullstackproject.services.WorkoutPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {
    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PostMapping("/{planId}/add/{workoutId}")
    public ResponseEntity<Void> addWorkoutToPlan(
            @PathVariable Integer workoutId,
            @PathVariable Integer planId) {
        workoutPlanService.addWorkoutToPlan(workoutId, planId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{planId}/remove/{workoutId}")
    public ResponseEntity<Void> deleteWorkoutFromPlan(
            @PathVariable Integer workoutId,
            @PathVariable Integer planId) {
        workoutPlanService.deleteWorkoutFromPlan(workoutId, planId);
        return ResponseEntity.ok().build();
    }
}