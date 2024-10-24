package source.code.controller.Workout;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Workout.WorkoutCreateDto;
import source.code.dto.response.WorkoutResponseDto;
import source.code.service.declaration.Workout.WorkoutService;

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

  @GetMapping
  public ResponseEntity<List<WorkoutResponseDto>> getAllWorkouts() {
    List<WorkoutResponseDto> workouts = workoutService.getAllWorkouts();
    return ResponseEntity.ok(workouts);
  }

  @GetMapping("/plan/{planId}")
  public ResponseEntity<List<WorkoutResponseDto>> getWorkoutsByPlan(@PathVariable int planId) {
    List<WorkoutResponseDto> workouts = workoutService.getWorkoutsByPlan(planId);
    return ResponseEntity.ok(workouts);
  }

  @PostMapping
  public ResponseEntity<WorkoutResponseDto> createWorkout(@Valid @RequestBody WorkoutCreateDto workoutDto) {
    WorkoutResponseDto response = workoutService.createWorkout(workoutDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}