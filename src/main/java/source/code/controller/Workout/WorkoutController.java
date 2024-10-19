package source.code.controller.Workout;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.WorkoutDto;
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
  public ResponseEntity<WorkoutDto> getWorkout(@PathVariable int id) {
    WorkoutDto workout = workoutService.getWorkout(id);
    return ResponseEntity.ok(workout);
  }

  @GetMapping
  public ResponseEntity<List<WorkoutDto>> getAllWorkouts() {
    List<WorkoutDto> workouts = workoutService.getAllWorkouts();
    return ResponseEntity.ok(workouts);
  }

  @GetMapping("/plan/{planId}")
  public ResponseEntity<List<WorkoutDto>> getWorkoutsByPlan(@PathVariable int planId) {
    List<WorkoutDto> workouts = workoutService.getWorkoutsByPlan(planId);
    return ResponseEntity.ok(workouts);
  }

  @PostMapping
  public ResponseEntity<WorkoutDto> createWorkout(@Valid @RequestBody WorkoutDto workoutDto) {
    WorkoutDto response = workoutService.createWorkout(workoutDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}