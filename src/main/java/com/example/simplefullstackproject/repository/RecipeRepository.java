package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}