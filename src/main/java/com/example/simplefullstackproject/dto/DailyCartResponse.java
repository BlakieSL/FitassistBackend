package com.example.simplefullstackproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartResponse {
    private List<FoodDtoResponse> foods;
    private double totalCalories;
    private double totalCarbohydrates;
    private double totalProtein;
    private double totalFat;
}
