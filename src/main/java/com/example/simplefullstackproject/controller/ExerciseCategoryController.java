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

    @GetMapping("/{categoryId}/exercises")
    public ResponseEntity<List<ExerciseDto>> getExercisesByCategoryId(@PathVariable int categoryId){
        return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId));
    }
}
