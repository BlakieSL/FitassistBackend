package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String text;

    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Media> media;

    @ManyToMany(mappedBy = "exercises")
    private Set<User> users = new HashSet<>();

    @OneToOne(mappedBy = "exercise")
    private WorkoutSet workoutSet;
}
