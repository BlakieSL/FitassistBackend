package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_cart_activity")
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private int time;

    @ManyToOne
    @JoinColumn(name = "Daily_Activity_Id", nullable = false)
    private DailyActivity dailyCartActivity;
}
