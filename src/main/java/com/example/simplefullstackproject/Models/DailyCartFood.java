package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

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
    @JoinColumn(name = "Daily_Cart_Id", nullable = false)
    private DailyCart dailyCartFood;

    @ManyToOne
    @JoinColumn(name = "Food_Id", nullable = false)
    private Food food;
}
