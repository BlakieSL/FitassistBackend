package com.example.simplefullstackproject.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link com.example.simplefullstackproject.models.User}
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