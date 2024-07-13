package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.UserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Integer> {
}