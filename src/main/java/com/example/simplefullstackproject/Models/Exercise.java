package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_TEXT_LENGTH = 1000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @NotBlank
    @Size(max = MAX_TEXT_LENGTH)
    @Column(nullable = false, length = MAX_TEXT_LENGTH)
    private String text;

    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Media> media;

    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserExercise> user_exercise;

    @OneToOne(mappedBy = "exercise")
    private WorkoutSet workoutSet;
}
