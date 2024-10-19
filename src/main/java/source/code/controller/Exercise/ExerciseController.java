package source.code.controller.Exercise;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Exercise.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.enumerators.ExerciseField;
import source.code.service.declaration.Exercise.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
  private final ExerciseService exerciseService;

  public ExerciseController(ExerciseService exerciseService) {
    this.exerciseService = exerciseService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable int id) {
    ExerciseResponseDto exercise = exerciseService.getExercise(id);
    return ResponseEntity.ok(exercise);
  }

  @GetMapping
  public ResponseEntity<List<ExerciseResponseDto>> getAllExercises() {
    List<ExerciseResponseDto> exercises = exerciseService.getAllExercises();
    return ResponseEntity.ok(exercises);
  }

  @PostMapping
  public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody ExerciseCreateDto dto) {
    ExerciseResponseDto savedExercise = exerciseService.createExercise(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
  }

  @PostMapping("/search")
  public ResponseEntity<List<ExerciseResponseDto>> searchExercises(
          @Valid @RequestBody SearchRequestDto request) {
    return ResponseEntity.ok(exerciseService.searchExercises(request));
  }

  @GetMapping("/{categoryId}/categories")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByCategory(
          @PathVariable int categoryId) {

    return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId));
  }

  @GetMapping("/field/{field}/{value}")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByField(
          @PathVariable ExerciseField field,
          @PathVariable int value) {
    List<ExerciseResponseDto> exercises = exerciseService.getExercisesByField(field, value);
    return ResponseEntity.ok(exercises);
  }
}