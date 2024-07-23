package com.example.simplefullstackproject.Dtos;

import com.example.simplefullstackproject.Validations.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.simplefullstackproject.Models.User}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest implements Serializable {
    @Size(max = 40)
    @NotBlank
    private String name;
    @Size(max = 40)
    @NotBlank
    private String surname;
    @Size(max = 50)
    @Email
    @NotBlank
    @UniqueEmailDomain
    private String email;
    @Size(min = 8, max = 255)
    @NotBlank
    @PasswordDigitsDomain
    @PasswordUppercaseDomain
    @PasswordSpecialDomain
    @PasswordLowercaseDomain
    private String password;
    @Size(min = 4, max = 6)
    @NotBlank
    private String gender;
    @NotNull
    @Past
    private LocalDate birthday;
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