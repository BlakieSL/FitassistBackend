package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.ExerciseCategoryDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.service.ExerciseService;
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
    public ResponseEntity<List<ExerciseCategoryDto>> getAllExerciseCategories() {
        return ResponseEntity.ok(exerciseService.getCategories());
    }

    @GetMapping("/{categoryId}/categories")
    public ResponseEntity<List<ExerciseDto>> getExercisesByCategoryId(@PathVariable int categoryId){
        return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId));
    }

    @GetMapping("/{expertiseLevelId}/expertise-level")
    public ResponseEntity<List<ExerciseDto>> getExercisesByExpertiseLevel(
            @PathVariable int expertiseLevelId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByExpertiseLevel(expertiseLevelId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{forceTypeId}/force-type")
    public ResponseEntity<List<ExerciseDto>> getExercisesByForceType(
            @PathVariable int forceTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByForceType(forceTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{mechanicsTypeId}/mechanics-type")
    public ResponseEntity<List<ExerciseDto>> getExercisesByMechanicsType(
            @PathVariable int mechanicsTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByMechanicsType(mechanicsTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseEquipmentId}/equipment")
    public ResponseEntity<List<ExerciseDto>> getExercisesByEquipment(
            @PathVariable int exerciseEquipmentId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByEquipment(exerciseEquipmentId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseTypeId}/type")
    public ResponseEntity<List<ExerciseDto>> getExercisesByType(
            @PathVariable int exerciseTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByType(exerciseTypeId);
        return ResponseEntity.ok(exercises);
    }
}
