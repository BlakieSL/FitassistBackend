package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.WorkoutDto;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.WorkoutMapper;
import com.example.simplefullstackproject.model.Workout;
import com.example.simplefullstackproject.model.WorkoutPlan;
import com.example.simplefullstackproject.model.WorkoutType;
import com.example.simplefullstackproject.repository.WorkoutPlanRepository;
import com.example.simplefullstackproject.repository.WorkoutRepository;
import com.example.simplefullstackproject.repository.WorkoutTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutService {
    private final ValidationHelper validationHelper;
    private final WorkoutMapper workoutMapper;
    private final WorkoutRepository workoutRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutService(ValidationHelper validationHelper,
                          WorkoutMapper workoutMapper,
                          WorkoutRepository workoutRepository,
                          WorkoutPlanRepository workoutPlanRepository,
                          WorkoutTypeRepository workoutTypeRepository) {
        this.validationHelper = validationHelper;
        this.workoutMapper = workoutMapper;
        this.workoutRepository = workoutRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutTypeRepository = workoutTypeRepository;
    }

    @Transactional
    public WorkoutDto saveWorkout(WorkoutDto dto) {
        validationHelper.validate(dto);

        WorkoutType workoutType = workoutTypeRepository
                .findById(dto.getWorkoutTypeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout type with id: " +
                                dto.getWorkoutTypeId() + " not found"));
        Workout workout = workoutMapper.toEntity(dto);
        workout.setWorkoutType(workoutType);

        Workout savedWorkout = workoutRepository.save(workout);
        return workoutMapper.toDto(savedWorkout);
    }

    public WorkoutDto getWorkoutById(int id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout with id: " + id + " not found"));
        return workoutMapper.toDto(workout);
    }

    public List<WorkoutDto> getWorkouts() {
        List<Workout> workouts = workoutRepository.findAll();
        return workouts.stream()
                .map(workoutMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<WorkoutDto> getWorkoutsByPlanID(int planId) {
        List<WorkoutPlan> planWorkouts = workoutPlanRepository
                .findByPlanId(planId);
        List<Workout> workouts = planWorkouts
                .stream()
                .map(WorkoutPlan::getWorkout)
                .collect(Collectors.toList());
        return workouts
                .stream()
                .map(workoutMapper::toDto)
                .collect(Collectors.toList());
    }
}