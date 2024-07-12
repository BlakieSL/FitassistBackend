package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDtoResponse {
    @NotNull
    private Integer id;
    @Size(max = 50)
    @NotBlank
    private String name;
    @NotNull
    @Positive
    private int caloriesBurn;
    @NotNull
    @Positive
    private int time;
}
