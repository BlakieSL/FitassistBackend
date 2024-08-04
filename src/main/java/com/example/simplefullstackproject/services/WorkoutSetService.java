package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.WorkoutSetDto;
import com.example.simplefullstackproject.models.WorkoutSet;
import com.example.simplefullstackproject.models.WorkoutType;
import com.example.simplefullstackproject.models.Exercise;
import com.example.simplefullstackproject.repositories.WorkoutSetRepository;
import com.example.simplefullstackproject.repositories.WorkoutTypeRepository;
import com.example.simplefullstackproject.repositories.ExerciseRepository;
import com.example.simplefullstackproject.services.Mappers.WorkoutSetDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutSetService {
    private final ValidationHelper validationHelper;
    private final WorkoutSetDtoMapper workoutSetDtoMapper;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutSetService(ValidationHelper validationHelper,
                             WorkoutSetDtoMapper workoutSetDtoMapper,
                             WorkoutSetRepository workoutSetRepository,
                             WorkoutTypeRepository workoutTypeRepository,
                             ExerciseRepository exerciseRepository) {
        this.validationHelper = validationHelper;
        this.workoutSetDtoMapper = workoutSetDtoMapper;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutTypeRepository = workoutTypeRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public WorkoutSetDto saveWorkoutSet(WorkoutSetDto workoutSetDto) {
        validationHelper.validate(workoutSetDto);

        WorkoutType workoutType = workoutTypeRepository
                .findById(workoutSetDto.getWorkoutTypeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout type with id: " +
                                workoutSetDto.getWorkoutTypeId() + " not found"));

        Exercise exercise = exerciseRepository
                .findById(workoutSetDto.getExerciseId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " +
                                workoutSetDto.getExerciseId() + " not found"));

        WorkoutSet workoutSet = workoutSetDtoMapper.map(workoutSetDto);
        workoutSet.setWorkoutType(workoutType);
        workoutSet.setExercise(exercise);

        WorkoutSet savedWorkoutSet = workoutSetRepository.save(workoutSet);
        return workoutSetDtoMapper.map(savedWorkoutSet);
    }

    public WorkoutSetDto getWorkoutSetById(Integer id) {
        WorkoutSet workoutSet = workoutSetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "WorkoutSet with id: " + id + " not found"));
        return workoutSetDtoMapper.map(workoutSet);
    }

    public List<WorkoutSetDto> getWorkoutSets() {
        List<WorkoutSet> workoutSets = workoutSetRepository.findAll();
        return workoutSets.stream()
                .map(workoutSetDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<WorkoutSetDto> getWorkoutSetsByWorkoutTypeId(Integer workoutTypeId) {
        List<WorkoutSet> workoutSets = workoutSetRepository
                .findByWorkoutTypeId(workoutTypeId);
        return workoutSets.stream()
                .map(workoutSetDtoMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWorkoutSetById(Integer id) {
        WorkoutSet workoutSet = workoutSetRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "WorkoutSet with id: " + id + " not found"));
        workoutSetRepository.delete(workoutSet);
    }
}