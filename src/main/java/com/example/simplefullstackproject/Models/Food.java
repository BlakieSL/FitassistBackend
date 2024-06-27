package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "food")
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
    @Positive
    @Column(nullable = false)
    private Double protein;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double fat;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double carbohydrates;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
