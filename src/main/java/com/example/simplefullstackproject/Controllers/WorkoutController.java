package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.WorkoutDto;
import com.example.simplefullstackproject.Services.WorkoutService;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutDto> saveWorkout(
            @Valid @RequestBody WorkoutDto workoutDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        WorkoutDto response = workoutService.saveWorkout(workoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDto> getWorkoutById(@PathVariable Integer id) {
        WorkoutDto workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDto>> getWorkouts() {
        List<WorkoutDto> workouts = workoutService.getWorkouts();
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<WorkoutDto>> getWorkoutsByPlanID(@PathVariable Integer planId) {
        List<WorkoutDto> workouts = workoutService.getWorkoutsByPlanID(planId);
        return ResponseEntity.ok(workouts);
    }
}