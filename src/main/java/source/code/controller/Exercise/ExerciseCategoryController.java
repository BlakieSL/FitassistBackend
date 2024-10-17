package source.code.controller.Exercise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.enumerators.ExerciseField;
import source.code.service.declaration.ExerciseCategoryService;
import source.code.service.declaration.ExerciseService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/exercise-categories")
public class ExerciseCategoryController {
  private final ExerciseCategoryService exerciseCategoryService;

  public ExerciseCategoryController(ExerciseCategoryService exerciseCategoryService) {
    this.exerciseCategoryService = exerciseCategoryService;
  }

  @GetMapping
  public ResponseEntity<List<ExerciseCategoryResponseDto>> getAllExerciseCategories() {
    return ResponseEntity.ok(exerciseCategoryService.getAllCategories());
  }
}
