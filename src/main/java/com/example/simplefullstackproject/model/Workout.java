package com.example.simplefullstackproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
    private static final int NAME_MAX_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private int time;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.REMOVE)
    private final Set<WorkoutPlan> workoutPlans = new HashSet<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_type_id", nullable = false)
    private WorkoutType workoutType;
}
