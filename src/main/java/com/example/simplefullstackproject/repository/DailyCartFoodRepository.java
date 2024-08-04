package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.DailyCartFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {
}