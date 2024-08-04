package com.example.simplefullstackproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDtoResponse {
    private Integer id;
    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private Integer categoryId;
    private int amount;
}
