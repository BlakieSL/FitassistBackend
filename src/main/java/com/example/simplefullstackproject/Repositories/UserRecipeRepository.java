package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.UserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    List<UserRecipe> findByUserId(Integer userId);
    Optional<UserRecipe> findByUserIdAndRecipeId(Integer userId, Integer recipeId);
    boolean existsByUserIdAndRecipeId(Integer userId, Integer recipeId);
}