package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findAllByRecipeCategory_Id(Integer categoryId);
}