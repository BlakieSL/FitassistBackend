package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyCartFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {
}