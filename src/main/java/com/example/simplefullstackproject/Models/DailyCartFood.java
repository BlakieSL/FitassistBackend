package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_cart_food")
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
    @JoinColumn(name = "Daily_Cart_Id", nullable = false)
    private DailyCart dailyCartFood;
}
