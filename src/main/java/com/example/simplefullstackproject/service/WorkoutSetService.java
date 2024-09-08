package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.WorkoutSetDto;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.WorkoutSetMapper;
import com.example.simplefullstackproject.model.WorkoutSet;
import com.example.simplefullstackproject.model.WorkoutType;
import com.example.simplefullstackproject.model.Exercise;
import com.example.simplefullstackproject.repository.WorkoutSetRepository;
import com.example.simplefullstackproject.repository.WorkoutTypeRepository;
import com.example.simplefullstackproject.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutSetService {
    private final ValidationHelper validationHelper;
    private final WorkoutSetMapper workoutSetMapper;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutSetService(ValidationHelper validationHelper,
                             WorkoutSetMapper workoutSetMapper,
                             WorkoutSetRepository workoutSetRepository,
                             WorkoutTypeRepository workoutTypeRepository,
                             ExerciseRepository exerciseRepository) {
        this.validationHelper = validationHelper;
        this.workoutSetMapper = workoutSetMapper;
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

        WorkoutSet workoutSet = workoutSetMapper.toEntity(workoutSetDto);
        workoutSet.setWorkoutType(workoutType);
        workoutSet.setExercise(exercise);

        WorkoutSet savedWorkoutSet = workoutSetRepository.save(workoutSet);
        return workoutSetMapper.toDto(savedWorkoutSet);
    }

    public WorkoutSetDto getWorkoutSetById(Integer id) {
        WorkoutSet workoutSet = workoutSetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "WorkoutSet with id: " + id + " not found"));
        return workoutSetMapper.toDto(workoutSet);
    }

    public List<WorkoutSetDto> getWorkoutSets() {
        List<WorkoutSet> workoutSets = workoutSetRepository.findAll();
        return workoutSets.stream()
                .map(workoutSetMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<WorkoutSetDto> getWorkoutSetsByWorkoutTypeId(Integer workoutTypeId) {
        List<WorkoutSet> workoutSets = workoutSetRepository
                .findByWorkoutTypeId(workoutTypeId);
        return workoutSets.stream()
                .map(workoutSetMapper::toDto)
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