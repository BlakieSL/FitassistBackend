package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.WorkoutDto;
import com.example.simplefullstackproject.model.Workout;
import org.springframework.stereotype.Service;

@Service
public class WorkoutDtoMapper {
    public WorkoutDto map(Workout workout){
        return new WorkoutDto(
                workout.getId(),
                workout.getName(),
                workout.getTime(),
                workout.getWorkoutType().getId()
        );
    }

    public Workout map(WorkoutDto workoutDto){
        Workout workout = new Workout();
        workout.setName(workoutDto.getName());
        workout.setTime(workoutDto.getTime());
        return workout;
    }
}
