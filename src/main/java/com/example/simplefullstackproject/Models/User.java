package com.example.simplefullstackproject.Models;

import com.example.simplefullstackproject.Validations.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    @UniqueEmailDomain(groups = ValidationGroups.Registration.class)
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
    @Positive
    @Column(nullable = false)
    private int age;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double height;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double weight;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double calculatedCalories;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private DailyCart dailyCart;

    @OneToOne(mappedBy = "user")
    private DailyActivity dailyActivity;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Recipe> recipes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Exercise> exercises = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Plan> plans = new HashSet<>();
}
