package com.example.simplefullstackproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan_expertise_level")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanExpertiseLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "planExpertiseLevel", cascade = CascadeType.REMOVE)
    private final Set<Plan> plans = new HashSet<>();
}
