package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    List<Food> findAllByNameContainingIgnoreCase(String name);
    List<Food> findAllByFoodCategory_Id(Integer categoryId);
}