package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link com.example.simplefullstackproject.Models.User}
 */
@Getter
@Setter
public class UserLoginRequest implements Serializable {
    @Size(max = 50)
    @Email
    @NotBlank
    private String email;
    @Size(min = 8, max = 255)
    @NotBlank
    private String password;
}