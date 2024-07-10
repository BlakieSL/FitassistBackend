package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDtoResponse {
    @NotNull
    Integer id;
    @Size(max = 50)
    @NotBlank
    String name;
    @NotNull
    @Positive
    Double calories;
    @NotNull
    @PositiveOrZero
    Double protein;
    @NotNull
    @PositiveOrZero
    Double fat;
    @NotNull
    @PositiveOrZero
    Double carbohydrates;
    @NotNull
    Integer categoryId;
    @NotNull
    @Positive
    Integer amount;
}
