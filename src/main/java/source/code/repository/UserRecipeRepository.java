package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.recipe.RecipeSummaryDto;
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

    @Query("""
           SELECT new source.code.dto.response.recipe.RecipeSummaryDto(
               r.id,
               r.name,
               r.description,
               r.isPublic,
               u.username,
               u.id,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = u.id
                AND m.parentType = 'USER'
                ORDER BY m.id ASC
                LIMIT 1),
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = r.id
                AND m.parentType = 'RECIPE'
                ORDER BY m.id ASC
                LIMIT 1),
               null,
               CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'LIKE') AS int),
               CAST((SELECT COUNT(ur3) FROM UserRecipe ur3 WHERE ur3.recipe.id = r.id AND ur3.type = 'SAVE') AS int),
               r.views,
               CAST((SELECT COUNT(rf) FROM RecipeFood rf WHERE rf.recipe.id = r.id) AS int))
           FROM UserRecipe ur
           JOIN ur.recipe r
           JOIN r.user u
           WHERE ur.user.id = :userId
           AND ur.type = :type
           AND r.isPublic = true
           """)
    List<RecipeSummaryDto> findRecipeSummaryByUserIdAndType(@Param("userId") int userId, @Param("type") TypeOfInteraction type);

    Optional<UserRecipe> findByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    boolean existsByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    long countByRecipeIdAndType(int recipeId, TypeOfInteraction type);
}