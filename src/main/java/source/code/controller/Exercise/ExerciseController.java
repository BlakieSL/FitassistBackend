package source.code.controller.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.Exercise.ExerciseCreateDto;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Response.ExerciseResponseDto;
import source.code.service.Declaration.Exercise.ExerciseService;

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

  @PostMapping("/filter")
  public ResponseEntity<List<ExerciseResponseDto>> getFilteredExercises(
          @Valid @RequestBody FilterDto filter) {
    List<ExerciseResponseDto> filtered = exerciseService.getFilteredExercises(filter);
    return ResponseEntity.ok(filtered);
  }

  @GetMapping("/{categoryId}/categories")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByCategory(
          @PathVariable int categoryId) {

    return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId));
  }

  @PostMapping
  public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody ExerciseCreateDto dto) {
    ExerciseResponseDto savedExercise = exerciseService.createExercise(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateExercise(@PathVariable int id, @RequestBody JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    exerciseService.updateExercise(id, patch);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExercise(@PathVariable int id) {
    exerciseService.deleteExercise(id);
    return ResponseEntity.noContent().build();
  }
}