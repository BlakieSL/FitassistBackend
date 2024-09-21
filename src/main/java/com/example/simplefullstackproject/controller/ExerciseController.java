package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.ExerciseService;
import com.example.simplefullstackproject.service.UserExerciseService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ExerciseDto> getExerciseById(
            @PathVariable int id
    ) {
        ExerciseDto exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> getAllExercises() {
        List<ExerciseDto> exercises = exerciseService.getExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByUserId(
            @PathVariable int userId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByUserID(userId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavedDto> getLikesAndSavesExercise(
            @PathVariable int id
    ) {
        LikesAndSavedDto dto = userExerciseService.calculateLikesAndSavesByExerciseId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/instructions")
    public ResponseEntity<List<ExerciseInstructionDto>> getAllInstructionsByExerciseId(
            @PathVariable int id
    ) {
        List<ExerciseInstructionDto> instructions = exerciseService.getExerciseInstructions(id);
        return ResponseEntity.ok(instructions);
    }

    @GetMapping("/{id}/tips")
    public ResponseEntity<List<ExerciseTipDto>> getAllTipsByExerciseId(
            @PathVariable int id
    ) {
        List<ExerciseTipDto> tips = exerciseService.getExerciseTips(id);
        return  ResponseEntity.ok(tips);
    }
    @PostMapping
    public ResponseEntity<ExerciseDto> saveExercise(
            @Valid @RequestBody ExerciseAdditionDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        ExerciseDto savedExercise = exerciseService.saveExercise(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ExerciseDto>> searchExercises(
            @Valid @RequestBody SearchDtoRequest request,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        return ResponseEntity.ok(exerciseService.searchExercises(request));
    }
}