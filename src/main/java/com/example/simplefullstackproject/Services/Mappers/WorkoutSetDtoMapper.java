package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.WorkoutSetDto;
import com.example.simplefullstackproject.Models.WorkoutSet;
import org.springframework.stereotype.Service;

@Service
public class WorkoutSetDtoMapper {
    public WorkoutSetDto map(WorkoutSet workoutSet) {
        return new WorkoutSetDto(
                workoutSet.getId(),
                workoutSet.getWeight(),
                workoutSet.getRepetitions(),
                workoutSet.getWorkoutType().getId(),
                workoutSet.getExercise().getId()
        );
    }

    public WorkoutSet map(WorkoutSetDto workoutSetDto) {
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWeight(workoutSetDto.getWeight());
        workoutSet.setRepetitions(workoutSetDto.getRepetitions());
        return workoutSet;
    }
}
