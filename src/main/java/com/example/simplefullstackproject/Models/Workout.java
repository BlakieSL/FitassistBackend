package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private int time;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Plan> plans = new HashSet<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_type_id", nullable = false)
    private WorkoutType workoutType;
}
