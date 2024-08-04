package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.DailyCartFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {
}