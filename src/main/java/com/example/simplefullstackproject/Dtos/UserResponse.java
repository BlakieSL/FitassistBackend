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
    @Size(max = 40)
    private String name;
    @Size(max = 40)
    private String surname;
    @Size(max = 50)
    @Email
    private String email;
    @Size(min = 4, max = 6)
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;
    private Double calculatedCalories;
}