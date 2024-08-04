package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDto {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private static final int MAX_TEXT_LENGTH = 1000;

    private Integer id;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    private String name;

    @NotBlank
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotBlank
    @Size(max = MAX_TEXT_LENGTH)
    private String text;
}
