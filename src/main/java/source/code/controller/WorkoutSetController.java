package source.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.workout.PublicPlanOwnerOrAdminAcceptWorkoutId;
import source.code.annotation.workoutSet.PublicPlanOwnerOrAdminAcceptWorkoutSetId;
import source.code.annotation.workoutSet.WorkoutSetOwnerOrAdmin;
import source.code.annotation.workoutSet.WorkoutSetOwnerOrAdminCreation;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.service.declaration.workoutSet.WorkoutSetService;

@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {

	private final WorkoutSetService workoutSetService;

	public WorkoutSetController(WorkoutSetService workoutSetService) {
		this.workoutSetService = workoutSetService;
	}

	@WorkoutSetOwnerOrAdminCreation
	@PostMapping
	public ResponseEntity<WorkoutSetResponseDto> createWorkoutSet(@RequestBody WorkoutSetCreateDto createDto) {
		WorkoutSetResponseDto response = workoutSetService.createWorkoutSet(createDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@WorkoutSetOwnerOrAdmin
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateWorkoutSet(@PathVariable int id, @RequestBody JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		workoutSetService.updateWorkoutSet(id, patch);
		return ResponseEntity.noContent().build();
	}

	@WorkoutSetOwnerOrAdmin
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWorkoutSet(@PathVariable int id) {
		workoutSetService.deleteWorkoutSet(id);
		return ResponseEntity.noContent().build();
	}

	@PublicPlanOwnerOrAdminAcceptWorkoutSetId
	@GetMapping("/{workoutSetId}")
	public ResponseEntity<WorkoutSetResponseDto> getWorkoutSet(@PathVariable int workoutSetId) {
		WorkoutSetResponseDto workoutSet = workoutSetService.getWorkoutSet(workoutSetId);
		return ResponseEntity.ok(workoutSet);
	}

	@PublicPlanOwnerOrAdminAcceptWorkoutId
	@GetMapping("/workouts/{workoutId}")
	public ResponseEntity<List<WorkoutSetResponseDto>> getAllWorkoutSetsForWorkout(@PathVariable int workoutId) {
		List<WorkoutSetResponseDto> workoutSets = workoutSetService.getAllWorkoutSetsForWorkout(workoutId);
		return ResponseEntity.ok(workoutSets);
	}

}
