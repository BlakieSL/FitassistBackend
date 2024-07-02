package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_cart")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @OneToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "dailyCartFood", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DailyCartFood> dailyCartFoods = new ArrayList<>();
}
