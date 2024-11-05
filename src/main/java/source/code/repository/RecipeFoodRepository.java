package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.recipe.RecipeFood;

import java.util.List;
import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Integer> {
    List<RecipeFood> findByRecipeId(int recipeId);

    List<RecipeFood> findByFoodId(int foodId);

    boolean existsByRecipeIdAndFoodId(int recipeId, int foodId);

    Optional<RecipeFood> findByRecipeIdAndFoodId(int recipeId, int foodId);
}