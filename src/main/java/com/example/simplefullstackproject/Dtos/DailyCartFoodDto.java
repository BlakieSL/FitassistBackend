package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyCartFoodDto {
    @NotNull
    private Integer id;
    @NotNull
    @Positive
    private Integer amount;
}
