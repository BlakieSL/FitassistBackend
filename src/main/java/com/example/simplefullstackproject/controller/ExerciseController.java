package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.ExerciseAdditionDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.dto.SearchDtoRequest;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.ExerciseService;
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

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> getAllExercises() {
        List<ExerciseDto> exercises = exerciseService.getExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByUserId(
            @PathVariable int userId) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByUserID(userId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> getExerciseById(
            @PathVariable int id) {
        ExerciseDto exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @PostMapping
    public ResponseEntity<ExerciseDto> saveExercise(
            @Valid @RequestBody ExerciseAdditionDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ExerciseDto savedExercise = exerciseService.saveExercise(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ExerciseDto>> searchExercises(
            @Valid @RequestBody SearchDtoRequest request,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(exerciseService.searchExercises(request));
    }
}