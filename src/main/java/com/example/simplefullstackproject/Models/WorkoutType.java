package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "workout_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String description;

    @OneToMany(mappedBy = "workoutType", cascade = CascadeType.ALL)
    private List<Workout> workouts;

    @OneToMany(mappedBy = "workoutType", cascade = CascadeType.ALL)
    private List<WorkoutSet> workoutSets;
}
