package source.code.controller.Exercise;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.declaration.ExerciseService;
import source.code.service.declaration.UserExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
  private final ExerciseService exerciseService;
  private final UserExerciseService userExerciseService;

  public ExerciseController(ExerciseService exerciseService, UserExerciseService userExerciseService) {
    this.exerciseService = exerciseService;
    this.userExerciseService = userExerciseService;
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

  @GetMapping("/user/{userId}/type/{type}")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByUserAndType(@PathVariable int userId,
                                                                             @PathVariable short type) {
    List<ExerciseResponseDto> exercises = exerciseService.getExercisesByUserAndType(userId, type);
    return ResponseEntity.ok(exercises);
  }

  @GetMapping("/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getExerciseLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userExerciseService.calculateExerciseLikesAndSaves(id);
    return ResponseEntity.ok(dto);
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
}