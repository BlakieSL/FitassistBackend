package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodAdditionDto {
    @Size(max = 50)
    @NotBlank
    private String name;
    @NotNull
    @Positive
    private double calories;
    @NotNull
    @PositiveOrZero
    private double protein;
    @NotNull
    @PositiveOrZero
    private double fat;
    @NotNull
    @PositiveOrZero
    private double carbohydrates;
    @NotBlank
    private int categoryId;
}
