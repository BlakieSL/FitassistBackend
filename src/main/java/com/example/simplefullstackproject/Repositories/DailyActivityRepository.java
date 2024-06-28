package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
}