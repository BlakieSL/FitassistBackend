package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.UserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    List<UserRecipe> findByUserId(int userId);
    Optional<UserRecipe> findByUserIdAndRecipeIdAndType(int userId, int recipeId, short type);
    boolean existsByUserIdAndRecipeIdAndType(int userId, int recipeId, short type);
    long countByRecipeIdAndType(int recipeId, short type);
}