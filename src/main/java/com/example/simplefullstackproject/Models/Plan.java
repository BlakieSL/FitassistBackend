package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private static final int NAME_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 255;
    private static final int TEXT_MAX_LENGTH = 10000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Size(max = DESCRIPTION_MAX_LENGTH)
    @Column(nullable = false)
    private String description;

    @NotBlank
    @Size(max = TEXT_MAX_LENGTH)
    @Column(nullable = false, length = TEXT_MAX_LENGTH)
    private String text;

    @OneToMany(mappedBy = "plan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<UserPlan> userPlans = new HashSet<>();

   @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<WorkoutPlan> workoutPlans = new HashSet<>();
}
