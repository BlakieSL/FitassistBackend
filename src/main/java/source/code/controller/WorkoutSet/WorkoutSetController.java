package source.code.controller.WorkoutSet;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.WorkoutSetDto;
import source.code.service.declaration.Workout.WorkoutSetService;

import java.util.List;

@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {
  private final WorkoutSetService workoutSetService;

  public WorkoutSetController(WorkoutSetService workoutSetService) {
    this.workoutSetService = workoutSetService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkoutSetDto> getWorkoutSet(@PathVariable int id) {
    WorkoutSetDto workoutSet = workoutSetService.getWorkoutSet(id);
    return ResponseEntity.ok(workoutSet);
  }

  @GetMapping
  public ResponseEntity<List<WorkoutSetDto>> getAllWorkoutSets() {
    List<WorkoutSetDto> workoutSets = workoutSetService.getAllWorkoutSets();
    return ResponseEntity.ok(workoutSets);
  }

  @GetMapping("/workout-type/{workoutTypeId}")
  public ResponseEntity<List<WorkoutSetDto>> getWorkoutSetsByWorkoutType(
          @PathVariable int workoutTypeId) {
    List<WorkoutSetDto> workoutSets = workoutSetService.getWorkoutSetsByWorkoutType(workoutTypeId);
    return ResponseEntity.ok(workoutSets);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkoutSetBy(@PathVariable int id) {
    workoutSetService.deleteWorkoutSet(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<WorkoutSetDto> createWorkoutSet(
          @Valid @RequestBody WorkoutSetDto workoutSetDto) {
    WorkoutSetDto response = workoutSetService.createWorkoutSet(workoutSetDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}