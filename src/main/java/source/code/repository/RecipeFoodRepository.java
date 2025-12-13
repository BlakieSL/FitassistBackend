package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.recipe.RecipeIngredientCountProjection;
import source.code.model.recipe.RecipeFood;

import java.util.List;
import java.util.Optional;

public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Integer> {
    @Query("""
        SELECT rf FROM RecipeFood rf
        JOIN FETCH rf.food f
        LEFT JOIN FETCH f.foodCategory
        LEFT JOIN FETCH f.mediaList
        WHERE rf.recipe.id = :recipeId
    """)
    List<RecipeFood> findByRecipeId(@Param("recipeId") int recipeId);

    boolean existsByRecipeIdAndFoodId(int recipeId, int foodId);

    Optional<RecipeFood> findByRecipeIdAndFoodId(int recipeId, int foodId);

    @Query("""
        SELECT rf.recipe.id as recipeId, COUNT(rf) as ingredientCount
        FROM RecipeFood rf
        WHERE rf.recipe.id IN :recipeIds
        GROUP BY rf.recipe.id
    """)
    List<RecipeIngredientCountProjection> countByRecipeIds(@Param("recipeIds") List<Integer> recipeIds);
}