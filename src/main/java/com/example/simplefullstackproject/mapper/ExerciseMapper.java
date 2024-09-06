package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.ExerciseCategoryDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.model.Exercise;
import com.example.simplefullstackproject.model.ExerciseCategory;
import com.example.simplefullstackproject.repository.ExerciseCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper {
    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;
    public abstract ExerciseDto toDto(Exercise exercise);

    @Mapping(target = "id", ignore = true)
    public abstract Exercise toEntity(ExerciseDto dto);

    public abstract ExerciseCategoryDto toCategoryDto(ExerciseCategory exerciseCategory);
}