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
public class PlanCategoryDto {
    private Integer id;
    private String name;
    private String iconUrl;
    private String gradient;
}
