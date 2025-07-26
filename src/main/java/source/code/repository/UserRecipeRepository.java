package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserRecipe;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Integer> {
    @Query("""
        SELECT ur FROM UserRecipe ur
        WHERE ur.recipe.isPublic = true AND ur.user.id = :userId AND ur.type = :type
""")
    List<UserRecipe> findByUserIdAndType(int userId, TypeOfInteraction type);

    Optional<UserRecipe> findByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    boolean existsByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    long countByRecipeIdAndType(int recipeId, TypeOfInteraction type);
}