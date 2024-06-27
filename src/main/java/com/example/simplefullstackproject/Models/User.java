package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String name;

    @NotBlank
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String surname;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(nullable = false, length = 50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min =4, max = 6)
    @Column(nullable = false, length = 6)
    private String gender;

    @NotNull
    private Integer age;

    @NotNull
    private Double height;

    @NotNull
    private Double weight;

    @NotNull
    private Double CalculatedCalories;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
}
