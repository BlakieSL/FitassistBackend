package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.model.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDto toDto(Exercise exercise);

    @Mapping(target = "id", ignore = true)
    Exercise toEntity(ExerciseDto dto);
}