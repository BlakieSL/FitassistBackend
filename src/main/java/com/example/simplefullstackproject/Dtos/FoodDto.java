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
    @NotBlank
    String categoryName;
}