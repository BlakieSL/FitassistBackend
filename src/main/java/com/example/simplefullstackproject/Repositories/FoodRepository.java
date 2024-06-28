package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Integer> {
}