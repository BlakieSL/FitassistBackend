package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link com.example.simplefullstackproject.model.User}
 */
@Getter
@Setter
public class UserUpdateRequest implements Serializable {
    @NotNull
    @Positive
    private double height;
    @NotNull
    @Positive
    private double weight;
    @NotBlank
    private String activityLevel;
    @NotBlank
    private String goal;
}