package com.fitassist.backend.controller;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.annotation.AdminOnly;
import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.exercise.ExerciseCategoriesResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.service.declaration.exercise.ExerciseService;
import jakarta.json.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

	private final ExerciseService exerciseService;

	public ExerciseController(ExerciseService exerciseService) {
		this.exerciseService = exerciseService;
	}

	@AdminOnly
	@PostMapping
	public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody ExerciseCreateDto dto) {
		ExerciseResponseDto savedExercise = exerciseService.createExercise(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
	}

	@AdminOnly
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateExercise(@PathVariable int id, @RequestBody JsonMergePatch patch)
			throws JacksonException {
		exerciseService.updateExercise(id, patch);
		return ResponseEntity.noContent().build();
	}

	@AdminOnly
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExercise(@PathVariable int id) {
		exerciseService.deleteExercise(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable int id) {
		ExerciseResponseDto exercise = exerciseService.getExercise(id);
		return ResponseEntity.ok(exercise);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<ExerciseSummaryDto>> getFilteredExercises(@Valid @RequestBody FilterDto filter,
			@PageableDefault(size = 100, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<ExerciseSummaryDto> filtered = exerciseService.getFilteredExercises(filter, pageable);
		return ResponseEntity.ok(filtered);
	}

	@GetMapping("/categories")
	public ResponseEntity<ExerciseCategoriesResponseDto> getAllExerciseCategories() {
		ExerciseCategoriesResponseDto categories = exerciseService.getAllExerciseCategories();
		return ResponseEntity.ok(categories);
	}

}
