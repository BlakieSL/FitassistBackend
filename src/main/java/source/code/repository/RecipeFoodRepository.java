package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Recipe.RecipeFood;

import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
  boolean existsByRecipeIdAndFoodId(int recipeId, int foodId);
  Optional<RecipeFood> findByRecipeIdAndFoodId(int recipeId, int foodId);
}