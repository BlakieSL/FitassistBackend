package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}