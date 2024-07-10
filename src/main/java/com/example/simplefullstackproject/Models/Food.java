package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double calories;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double protein;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double fat;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double carbohydrates;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DailyCartFood> dailyCartFoods = new ArrayList<>();

}
