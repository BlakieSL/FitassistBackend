package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "daily_cart_activity")
@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "Activity_Id", nullable = false)
    private Activity activity;
}
