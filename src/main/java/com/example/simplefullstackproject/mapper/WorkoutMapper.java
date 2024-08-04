package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.WorkoutDto;
import com.example.simplefullstackproject.model.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
    @Mapping(target = "workoutTypeId", source = "workoutType.id")
    WorkoutDto toDto(Workout workout);

    @Mapping(target = "id", ignore = true)
    Workout toEntity(WorkoutDto dto);
}
