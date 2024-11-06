package source.code.controller.WorkoutSetController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.Response.WorkoutSet.WorkoutSetResponseDto;
import source.code.service.declaration.workoutSet.WorkoutSetService;

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

    @GetMapping("/workouts/{workoutId}")
    public ResponseEntity<List<WorkoutSetResponseDto>> getAllWorkoutSetsForWorkout(
            @PathVariable int workoutId
    ) {
        List<WorkoutSetResponseDto> workoutSets = workoutSetService
                .getAllWorkoutSetsForWorkout(workoutId);
        return ResponseEntity.ok(workoutSets);
    }

    @PostMapping
    public ResponseEntity<WorkoutSetResponseDto> createWorkoutSet(
            @RequestBody WorkoutSetCreateDto workoutSetDto
    ) {
        WorkoutSetResponseDto response = workoutSetService.createWorkoutSet(workoutSetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateWorkoutSet(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        workoutSetService.updateWorkoutSet(id, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutSet(@PathVariable int id) {
        workoutSetService.deleteWorkoutSet(id);
        return ResponseEntity.noContent().build();
    }
}
