package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.RecipeFood;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
    Optional<RecipeFood> findByRecipeIdAndFoodId(Integer recipeId, Integer foodId);
}