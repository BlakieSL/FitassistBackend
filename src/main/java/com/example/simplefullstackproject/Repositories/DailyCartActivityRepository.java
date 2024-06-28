package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyCartActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartActivityRepository extends JpaRepository<DailyCartActivity, Integer> {
}