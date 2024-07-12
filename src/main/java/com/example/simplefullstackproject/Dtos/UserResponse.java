package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link com.example.simplefullstackproject.Models.User}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private String name;
    private String surname;
    private String email;
    private String gender;
    private int age;
    private double height;
    private double weight;
    private double calculatedCalories;
}