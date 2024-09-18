package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.ExerciseAdditionDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.dto.LikesAndSavedDto;
import com.example.simplefullstackproject.dto.SearchDtoRequest;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.ExerciseService;
import com.example.simplefullstackproject.service.UserExerciseService;
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

    @GetMapping("/expertise/{expertiseLevelId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByExpertiseLevel(
            @PathVariable int expertiseLevelId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByExpertiseLevel(expertiseLevelId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/force/{forceTypeId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByForceType(
            @PathVariable int forceTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByForceType(forceTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/mechanics/{mechanicsTypeId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByMechanicsType(
            @PathVariable int mechanicsTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByMechanicsType(mechanicsTypeId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/equipment/{exerciseEquipmentId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByEquipment(
            @PathVariable int exerciseEquipmentId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByEquipment(exerciseEquipmentId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/type/{exerciseTypeId}")
    public ResponseEntity<List<ExerciseDto>> getExercisesByType(
            @PathVariable int exerciseTypeId
    ) {
        List<ExerciseDto> exercises = exerciseService.getExercisesByType(exerciseTypeId);
        return ResponseEntity.ok(exercises);
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