package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}