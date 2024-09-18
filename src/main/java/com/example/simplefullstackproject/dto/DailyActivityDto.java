package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyActivityDto {
    @NotNull
    @Positive
    private int time;
}
