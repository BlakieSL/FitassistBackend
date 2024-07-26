package com.example.simplefullstackproject.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "daily_cart_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private int amount;

    @ManyToOne
    @JoinColumn(name = "daily_cart_id", nullable = false)
    @JsonBackReference
    private DailyCart dailyCartFood;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;
}
