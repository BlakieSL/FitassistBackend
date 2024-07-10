package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateCaloriesBurntRequest {
    @NotNull
    int userId;
    @NotNull
    @Positive
    int time = 1;
}
