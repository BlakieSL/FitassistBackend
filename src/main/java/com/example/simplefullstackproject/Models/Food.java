package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    private static final int MAX_NAME_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double calories;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double protein;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double fat;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double carbohydrates;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DailyCartFood> dailyCartFoods = new ArrayList<>();

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<RecipeFood> recipeFoods = new HashSet<>();
}
