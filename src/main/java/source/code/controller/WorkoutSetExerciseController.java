package source.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.workoutSet.PublicPlanOwnerOrAdminAcceptWorkoutSetId;
import source.code.annotation.workoutSetExercise.PublicPlanOwnerOrAdminAcceptWorkoutSetExerciseId;
import source.code.annotation.workoutSetExercise.WorkoutSetExerciseOwnerOrAdmin;
import source.code.annotation.workoutSetExercise.WorkoutSetExerciseOwnerOrAdminCreation;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;
import source.code.service.declaration.workoutSetExercise.WorkoutSetExerciseService;

@RestController
@RequestMapping("/api/workout-set-exercises")
public class WorkoutSetExerciseController {

	private final WorkoutSetExerciseService workoutSetExerciseService;

	public WorkoutSetExerciseController(WorkoutSetExerciseService workoutSetExerciseService) {
		this.workoutSetExerciseService = workoutSetExerciseService;
	}

	@WorkoutSetExerciseOwnerOrAdminCreation
	@PostMapping
	public ResponseEntity<WorkoutSetExerciseResponseDto> createWorkoutSetExercise(
			@RequestBody WorkoutSetExerciseCreateDto createDto) {
		WorkoutSetExerciseResponseDto response = workoutSetExerciseService.createWorkoutSetExercise(createDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@WorkoutSetExerciseOwnerOrAdmin
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateWorkoutSetExercise(@PathVariable int id, @RequestBody JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		workoutSetExerciseService.updateWorkoutSetExercise(id, patch);
		return ResponseEntity.noContent().build();
	}

	@WorkoutSetExerciseOwnerOrAdmin
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWorkoutSetExercise(@PathVariable int id) {
		workoutSetExerciseService.deleteWorkoutSetExercise(id);
		return ResponseEntity.noContent().build();
	}

	@PublicPlanOwnerOrAdminAcceptWorkoutSetExerciseId
	@GetMapping("/{workoutSetExerciseId}")
	public ResponseEntity<WorkoutSetExerciseResponseDto> getWorkoutSetExercise(@PathVariable int workoutSetExerciseId) {
		WorkoutSetExerciseResponseDto workoutSetExercise = workoutSetExerciseService
			.getWorkoutSetExercise(workoutSetExerciseId);
		return ResponseEntity.ok(workoutSetExercise);
	}

	@PublicPlanOwnerOrAdminAcceptWorkoutSetId
	@GetMapping("/workout-sets/{workoutSetId}")
	public ResponseEntity<List<WorkoutSetExerciseResponseDto>> getAllWorkoutSetExercisesForWorkoutSet(
			@PathVariable int workoutSetId) {
		List<WorkoutSetExerciseResponseDto> workoutSetExercises = workoutSetExerciseService
			.getAllWorkoutSetExercisesForWorkoutSet(workoutSetId);
		return ResponseEntity.ok(workoutSetExercises);
	}

}
