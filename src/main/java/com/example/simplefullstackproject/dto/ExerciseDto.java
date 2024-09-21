package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDto {
    private Integer id;
    private String name;
    private String description;
    private String text;
    private Double score;
    private ExerciseCategoryShortDto expertiseLevel;
    private ExerciseCategoryShortDto mechanicsType;
    private ExerciseCategoryShortDto forceType;
    private ExerciseCategoryShortDto exerciseEquipment;
    private ExerciseCategoryShortDto exerciseType;
    private List<ExerciseCategoryShortDto> categories;
}
