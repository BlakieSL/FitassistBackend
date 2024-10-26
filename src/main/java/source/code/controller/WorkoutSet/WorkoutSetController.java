package source.code.controller.WorkoutSet;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.Response.WorkoutSetResponseDto;
import source.code.service.Declaration.Workout.WorkoutSetService;

import java.util.List;

@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {
  private final WorkoutSetService workoutSetService;

  public WorkoutSetController(WorkoutSetService workoutSetService) {
    this.workoutSetService = workoutSetService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkoutSetResponseDto> getWorkoutSet(@PathVariable int id) {
    WorkoutSetResponseDto workoutSet = workoutSetService.getWorkoutSet(id);
    return ResponseEntity.ok(workoutSet);
  }

  @GetMapping
  public ResponseEntity<List<WorkoutSetResponseDto>> getAllWorkoutSets() {
    List<WorkoutSetResponseDto> workoutSets = workoutSetService.getAllWorkoutSets();
    return ResponseEntity.ok(workoutSets);
  }

  @GetMapping("/workout-type/{workoutTypeId}")
  public ResponseEntity<List<WorkoutSetResponseDto>> getWorkoutSetsByWorkoutType(
          @PathVariable int workoutTypeId) {
    List<WorkoutSetResponseDto> workoutSets = workoutSetService.getWorkoutSetsByWorkoutType(workoutTypeId);
    return ResponseEntity.ok(workoutSets);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkoutSetBy(@PathVariable int id) {
    workoutSetService.deleteWorkoutSet(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<WorkoutSetResponseDto> createWorkoutSet(
          @Valid @RequestBody WorkoutSetCreateDto request) {
    WorkoutSetResponseDto response = workoutSetService.createWorkoutSet(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}