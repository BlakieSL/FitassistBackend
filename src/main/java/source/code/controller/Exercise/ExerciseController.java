package source.code.controller.Exercise;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
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

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByUser(@PathVariable int userId) {
    List<ExerciseResponseDto> exercises = exerciseService.getExercisesByUser(userId);
    return ResponseEntity.ok(exercises);
  }

  @GetMapping("/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getExerciseLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userExerciseService.calculateExerciseLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{id}/instructions")
  public ResponseEntity<List<ExerciseInstructionResponseDto>> getAllInstructionsByExercise(
          @PathVariable int id) {
    List<ExerciseInstructionResponseDto> instructions = exerciseService.getExerciseInstructions(id);
    return ResponseEntity.ok(instructions);
  }

  @GetMapping("/{id}/tips")
  public ResponseEntity<List<ExerciseTipResponseDto>> getAllTipsByExercise(@PathVariable int id) {
    List<ExerciseTipResponseDto> tips = exerciseService.getExerciseTips(id);
    return ResponseEntity.ok(tips);
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