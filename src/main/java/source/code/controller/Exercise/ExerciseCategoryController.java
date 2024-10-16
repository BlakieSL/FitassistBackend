package source.code.controller.Exercise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.enumerators.ExerciseField;
import source.code.service.declaration.ExerciseService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/exercise-categories")
public class ExerciseCategoryController {
  private final ExerciseService exerciseService;

  public ExerciseCategoryController(ExerciseService exerciseService) {
    this.exerciseService = exerciseService;
  }

  @GetMapping
  public ResponseEntity<List<ExerciseCategoryResponseDto>> getAllExerciseCategories() {
    return ResponseEntity.ok(exerciseService.getAllCategories());
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
