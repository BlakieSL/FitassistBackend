package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.RecipeFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Integer> {
}