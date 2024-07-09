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
    Integer id;
    @Size(max = 50)
    @NotBlank
    String name;
    @NotNull
    @Positive
    Integer caloriesBurn;
    @NotNull
    @Positive
    Integer time;
}
