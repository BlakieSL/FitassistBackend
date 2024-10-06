package source.code.controller;

import source.code.dto.WorkoutSetDto;
import source.code.service.WorkoutSetService;
import source.code.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-sets")
public class WorkoutSetController {
    private final WorkoutSetService workoutSetService;

    public WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSetDto> getWorkoutSetById(@PathVariable int id) {
        WorkoutSetDto workoutSet = workoutSetService.getWorkoutSetById(id);
        return ResponseEntity.ok(workoutSet);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSetDto>> getWorkoutSets() {
        List<WorkoutSetDto> workoutSets = workoutSetService.getWorkoutSets();
        return ResponseEntity.ok(workoutSets);
    }

    @GetMapping("/workout-type/{workoutTypeId}")
    public ResponseEntity<List<WorkoutSetDto>> getWorkoutSetsByWorkoutTypeId(@PathVariable int workoutTypeId) {
        List<WorkoutSetDto> workoutSets = workoutSetService.getWorkoutSetsByWorkoutTypeId(workoutTypeId);
        return ResponseEntity.ok(workoutSets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutSetById(@PathVariable int id) {
        workoutSetService.deleteWorkoutSetById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<WorkoutSetDto> saveWorkoutSet(@Valid @RequestBody WorkoutSetDto workoutSetDto) {
        WorkoutSetDto response = workoutSetService.saveWorkoutSet(workoutSetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}