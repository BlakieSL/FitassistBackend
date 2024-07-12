package com.example.simplefullstackproject.Dtos;

import com.example.simplefullstackproject.Models.Category;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.example.simplefullstackproject.Models.Food}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto implements Serializable {
    private Integer id;
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
    private String categoryName;
}