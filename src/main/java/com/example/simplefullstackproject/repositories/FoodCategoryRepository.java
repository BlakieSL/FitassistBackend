package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {
    Optional<FoodCategory> findByName(String name);
}