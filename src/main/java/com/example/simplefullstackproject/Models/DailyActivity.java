package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "daily_activity")
@Data
public class DailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDate  date;

    @OneToOne(mappedBy = "dailyActivity")
    private User user;

    @OneToMany(mappedBy = "dailyCartActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<DailyCartActivity> dailyCartActivities = new ArrayList<>();
}
