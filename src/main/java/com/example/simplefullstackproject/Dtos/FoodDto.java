package com.example.simplefullstackproject.Dtos;

import com.example.simplefullstackproject.Models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Positive
    Double protein;
    @NotNull
    @Positive
    Double fat;
    @NotNull
    @Positive
    Double carbohydrates;
    @NotNull
    String categoryName;
}