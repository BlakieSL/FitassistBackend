package source.code.controller.workoutSetGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.WorkoutSetGroupOwnerOrAdmin;
import source.code.annotation.WorkoutSetGroupOwnerOrAdminCreation;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupCreateDto;
import source.code.dto.response.workoutSetGroup.WorkoutSetGroupResponseDto;
import source.code.service.declaration.workoutSetGroup.WorkoutSetGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/workout-set-groups")
public class WorkoutSetGroupController {
    private final WorkoutSetGroupService workoutSetGroupService;

    public WorkoutSetGroupController(WorkoutSetGroupService workoutSetGroupService) {
        this.workoutSetGroupService = workoutSetGroupService;
    }

    @WorkoutSetGroupOwnerOrAdminCreation
    @PostMapping
    public ResponseEntity<WorkoutSetGroupResponseDto> createWorkoutSetGroup(
            @RequestBody WorkoutSetGroupCreateDto createDto) {
        WorkoutSetGroupResponseDto response = workoutSetGroupService.createWorkoutSetGroup(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @WorkoutSetGroupOwnerOrAdmin
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateWorkoutSetGroup(
            @PathVariable int id,
            @RequestBody JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        workoutSetGroupService.updateWorkoutSetGroup(id, patch);
        return ResponseEntity.noContent().build();
    }

    @WorkoutSetGroupOwnerOrAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutSetGroup(@PathVariable int id) {
        workoutSetGroupService.deleteWorkoutSetGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSetGroupResponseDto> getWorkoutSetGroup(@PathVariable int id) {
        WorkoutSetGroupResponseDto workoutSetGroup = workoutSetGroupService.getWorkoutSetGroup(id);
        return ResponseEntity.ok(workoutSetGroup);
    }

    @GetMapping("/workouts/{workoutId}")
    public ResponseEntity<List<WorkoutSetGroupResponseDto>> getAllWorkoutSetGroupsForWorkout(
            @PathVariable int workoutId) {
        List<WorkoutSetGroupResponseDto> workoutSetGroups = workoutSetGroupService.getAllWorkoutSetGroupsForWorkout(workoutId);
        return ResponseEntity.ok(workoutSetGroups);
    }
}
