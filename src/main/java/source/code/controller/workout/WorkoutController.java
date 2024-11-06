package source.code.controller.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.workout.WorkoutCreateDto;
import source.code.dto.Response.workout.WorkoutResponseDto;
import source.code.service.declaration.workout.WorkoutService;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkout(@PathVariable int id) {
        WorkoutResponseDto workout = workoutService.getWorkout(id);
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<List<WorkoutResponseDto>> getAllWorkoutsForPlan(
            @PathVariable int planId
    ) {
        List<WorkoutResponseDto> workouts = workoutService.getAllWorkoutsForPlan(planId);
        return ResponseEntity.ok(workouts);
    }

    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(
            @Valid @RequestBody WorkoutCreateDto workoutDto
    ) {
        WorkoutResponseDto response = workoutService.createWorkout(workoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateWorkout(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        workoutService.updateWorkout(id, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable int id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }
}
