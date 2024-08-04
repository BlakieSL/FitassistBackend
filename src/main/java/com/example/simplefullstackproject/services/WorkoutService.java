package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.WorkoutDto;
import com.example.simplefullstackproject.models.Workout;
import com.example.simplefullstackproject.models.WorkoutPlan;
import com.example.simplefullstackproject.models.WorkoutType;
import com.example.simplefullstackproject.repositories.WorkoutPlanRepository;
import com.example.simplefullstackproject.repositories.WorkoutRepository;
import com.example.simplefullstackproject.repositories.WorkoutTypeRepository;
import com.example.simplefullstackproject.services.Mappers.WorkoutDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutService {
    private final ValidationHelper validationHelper;
    private final WorkoutDtoMapper workoutDtoMapper;
    private final WorkoutRepository workoutRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutService(ValidationHelper validationHelper,
                          WorkoutDtoMapper workoutDtoMapper,
                          WorkoutRepository workoutRepository,
                          WorkoutPlanRepository workoutPlanRepository,
                          WorkoutTypeRepository workoutTypeRepository) {
        this.validationHelper = validationHelper;
        this.workoutDtoMapper = workoutDtoMapper;
        this.workoutRepository = workoutRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutTypeRepository = workoutTypeRepository;
    }

    @Transactional
    public WorkoutDto saveWorkout(WorkoutDto workoutDto) {
        validationHelper.validate(workoutDto);

        WorkoutType workoutType = workoutTypeRepository
                .findById(workoutDto.getWorkoutTypeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout type with id: " +
                        workoutDto.getWorkoutTypeId() + " not found"));
        Workout workout = workoutDtoMapper.map(workoutDto);
        workout.setWorkoutType(workoutType);

        Workout savedWorkout = workoutRepository.save(workout);
        return workoutDtoMapper.map(workout);
    }

    public WorkoutDto getWorkoutById(Integer id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout with id: " + id + " not found"));
        return workoutDtoMapper.map(workout);
    }

    public List<WorkoutDto> getWorkouts() {
        List<Workout> workouts = workoutRepository.findAll();
        return workouts.stream()
                .map(workoutDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<WorkoutDto> getWorkoutsByPlanID(Integer planId) {
        List<WorkoutPlan> planWorkouts = workoutPlanRepository
                .findByPlanId(planId);
        List<Workout> workouts = planWorkouts
                .stream()
                .map(WorkoutPlan::getWorkout)
                .collect(Collectors.toList());
        return workouts
                .stream()
                .map(workoutDtoMapper::map)
                .collect(Collectors.toList());
    }
}