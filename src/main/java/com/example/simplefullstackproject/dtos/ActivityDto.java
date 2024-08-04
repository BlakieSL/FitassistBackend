package com.example.simplefullstackproject.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    @Positive
    private double met;
    @NotBlank
    private String categoryName;
}
