package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Recipe;
import com.example.simplefullstackproject.model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {
    Optional<RecipeCategory> findByName(String name);
}