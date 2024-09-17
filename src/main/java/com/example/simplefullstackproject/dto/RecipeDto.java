package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Integer id;
    private String name;
    private String description;
    private String text;
    List<RecipeCategoryShortDto> categories;
}
