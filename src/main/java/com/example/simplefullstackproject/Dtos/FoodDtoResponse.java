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
public class FoodDtoResponse {
    @Size(max = 50)
    @NotBlank
    String name;
    @NotNull
    @Positive
    Double calories;
    @NotNull
    @Positive
    Double protein;
    @NotNull
    @Positive
    Double fat;
    @NotNull
    @Positive
    Double carbohydrates;
    @NotNull
    Integer categoryId;
    @NotNull
    Integer amount;
}
