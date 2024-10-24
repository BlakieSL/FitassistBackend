package source.code.controller.WorkoutPlan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.service.declaration.Workout.WorkoutPlanService;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {
  private final WorkoutPlanService workoutPlanService;

  public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
    this.workoutPlanService = workoutPlanService;
  }

  @PostMapping("/{planId}/add/{workoutId}")
  public ResponseEntity<Void> addWorkoutToPlan(@PathVariable int workoutId,
                                               @PathVariable int planId) {
    workoutPlanService.addWorkoutToPlan(workoutId, planId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{planId}/remove/{workoutId}")
  public ResponseEntity<Void> deleteWorkoutFromPlan(@PathVariable int workoutId,
                                                    @PathVariable int planId) {
    workoutPlanService.deleteWorkoutFromPlan(workoutId, planId);
    return ResponseEntity.noContent().build();
  }
}