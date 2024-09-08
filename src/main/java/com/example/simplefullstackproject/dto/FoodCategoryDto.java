package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategoryDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String iconUrl;
    @NotBlank
    private String gradient;
}
