package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateAmountRequest {
    @NotNull
    @Positive
    private int amount = 100;
}
