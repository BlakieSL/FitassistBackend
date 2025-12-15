package source.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.plan.PublicPlanOrOwnerOrAdmin;
import source.code.annotation.workout.PublicPlanOwnerOrAdminAcceptWorkoutId;
import source.code.annotation.workout.WorkoutOwnerOrAdmin;
import source.code.annotation.workout.WorkoutOwnerOrAdminCreation;
import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.service.declaration.workout.WorkoutService;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @WorkoutOwnerOrAdminCreation
    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(
            @Valid @RequestBody WorkoutCreateDto workoutDto
    ) {
        WorkoutResponseDto response = workoutService.createWorkout(workoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @WorkoutOwnerOrAdmin
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateWorkout(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        workoutService.updateWorkout(id, patch);
        return ResponseEntity.noContent().build();
    }

    @WorkoutOwnerOrAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable int id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @PublicPlanOwnerOrAdminAcceptWorkoutId
    @GetMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponseDto> getWorkout(@PathVariable int workoutId) {
        WorkoutResponseDto workout = workoutService.getWorkout(workoutId);
        return ResponseEntity.ok(workout);
    }

    @PublicPlanOrOwnerOrAdmin
    @GetMapping("/plans/{planId}")
    public ResponseEntity<List<WorkoutResponseDto>> getAllWorkoutsForPlan(
            @PathVariable int planId
    ) {
        List<WorkoutResponseDto> workouts = workoutService.getAllWorkoutsForPlan(planId);
        return ResponseEntity.ok(workouts);
    }
}
