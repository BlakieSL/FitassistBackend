package com.example.simplefullstackproject.controllers;

import com.example.simplefullstackproject.dtos.ExerciseDto;
import com.example.simplefullstackproject.dtos.SearchDtoRequest;
import com.example.simplefullstackproject.exceptions.ValidationException;
import com.example.simplefullstackproject.services.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<ExerciseDto> saveExercise(@Valid @RequestBody ExerciseDto exerciseDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ExerciseDto savedExercise = exerciseService.saveExercise(exerciseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> getExerciseById(@PathVariable Integer id) {
        ExerciseDto exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> getAllExercises() {
        List<ExerciseDto> exercises = exerciseService.getExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByUserId(@PathVariable Integer userId) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByUserID(userId);
        return ResponseEntity.ok(exercises);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ExerciseDto>> searchExercises(
            @Valid @RequestBody SearchDtoRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(exerciseService.searchExercises(request));
    }
}