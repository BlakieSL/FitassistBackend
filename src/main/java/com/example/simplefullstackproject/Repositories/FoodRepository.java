package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    List<Food> findAllByNameContainingIgnoreCase(String name);
    List<Food> findAllByFoodCategory_Id(Integer categoryId);
}