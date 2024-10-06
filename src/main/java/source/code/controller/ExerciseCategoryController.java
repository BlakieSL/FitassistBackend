package source.code.controller;

import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.service.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(exerciseService.getCategories());
    }

    @GetMapping("/{categoryId}/categories")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByCategoryId(@PathVariable int categoryId){
        return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId));
    }

    @GetMapping("/{expertiseLevelId}/expertise-level")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByExpertiseLevel(@PathVariable int expertiseLevelId) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercisesByExpertiseLevel(expertiseLevelId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{forceTypeId}/force-type")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByForceType(@PathVariable int forceTypeId) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercisesByForceType(forceTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{mechanicsTypeId}/mechanics-type")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByMechanicsType(@PathVariable int mechanicsTypeId) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercisesByMechanicsType(mechanicsTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseEquipmentId}/equipment")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByEquipment(@PathVariable int exerciseEquipmentId) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercisesByEquipment(exerciseEquipmentId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseTypeId}/type")
    public ResponseEntity<List<ExerciseResponseDto>> getExercisesByType(@PathVariable int exerciseTypeId) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercisesByType(exerciseTypeId);
        return ResponseEntity.ok(exercises);
    }
}
