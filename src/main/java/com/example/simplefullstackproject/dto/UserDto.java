package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.example.simplefullstackproject.model.User}
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDto implements Serializable {
    @Size(max = 50)
    @Email
    private String email;
    @Size(min = 8, max = 255)
    private String password;
    private Set<String> roles;
}