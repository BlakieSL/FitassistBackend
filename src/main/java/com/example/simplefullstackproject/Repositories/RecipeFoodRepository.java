package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.RecipeFood;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
    Optional<RecipeFood> findByRecipeIdAndFoodId(Integer recipeId, Integer foodId);
}