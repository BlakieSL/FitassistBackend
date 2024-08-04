package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.WorkoutDto;
import com.example.simplefullstackproject.dto.WorkoutSetDto;
import com.example.simplefullstackproject.model.Workout;
import com.example.simplefullstackproject.model.WorkoutSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutSetMapper {
    @Mapping(target = "workoutTypeId", source = "workoutType.id")
    @Mapping(target = "exerciseId", source = "exercise.id")
    WorkoutSetDto toDto(WorkoutSet workoutSet);

    @Mapping(target = "id", ignore = true)
    WorkoutSet toEntity(WorkoutSetDto dto);
}
