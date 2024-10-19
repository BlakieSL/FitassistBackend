package source.code.controller.Exercise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
import source.code.service.declaration.Exercise.ExerciseInstructionsAndTipsService;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-instructions-and-tips")
public class ExerciseInstructionsAndTipsController {
  private final ExerciseInstructionsAndTipsService exerciseInstructionsAndTipsService;

  public ExerciseInstructionsAndTipsController(
          ExerciseInstructionsAndTipsService exerciseInstructionsAndTipsService) {
    this.exerciseInstructionsAndTipsService = exerciseInstructionsAndTipsService;
  }

  @GetMapping("/{id}/instructions")
  public ResponseEntity<List<ExerciseInstructionResponseDto>> getAllInstructionsByExercise(
          @PathVariable int id) {
    List<ExerciseInstructionResponseDto> instructions = exerciseInstructionsAndTipsService
            .getExerciseInstructions(id);
    return ResponseEntity.ok(instructions);
  }

  @GetMapping("/{id}/tips")
  public ResponseEntity<List<ExerciseTipResponseDto>> getAllTipsByExercise(@PathVariable int id) {
    List<ExerciseTipResponseDto> tips = exerciseInstructionsAndTipsService.getExerciseTips(id);
    return ResponseEntity.ok(tips);
  }
}
