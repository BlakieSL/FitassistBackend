package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_cart")
@AllArgsConstructor
@NoArgsConstructor
public class DailyCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer amount;

    @OneToOne(mappedBy = "dailyCart")
    private User user;

    @OneToMany(mappedBy = "dailyCartFood", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<DailyCartFood> dailyCartFoods = new ArrayList<>();
}
