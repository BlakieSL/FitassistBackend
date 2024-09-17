package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.RecipeFood;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
    Optional<RecipeFood> findByRecipeIdAndFoodId(int recipeId, int foodId);
}