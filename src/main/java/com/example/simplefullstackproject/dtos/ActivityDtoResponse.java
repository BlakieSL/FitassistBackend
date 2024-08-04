package com.example.simplefullstackproject.dtos;

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
    private double met;
    @NotBlank
    private String categoryName;
    @NotNull
    @Positive
    private int caloriesBurned;
    @NotNull
    @Positive
    private int time;
}
