package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.recipe.RecipeCountsProjection;
import source.code.dto.pojo.projection.recipe.RecipeInteractionDateProjection;
import source.code.dto.pojo.projection.recipe.RecipeUserInteractionProjection;
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

    @Query("""
        SELECT
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'LIKE' THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'DISLIKE' THEN 1 ELSE 0 END) as isDisliked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'SAVE' THEN 1 ELSE 0 END) as isSaved,
            SUM(CASE WHEN ur.type = 'LIKE' THEN 1 ELSE 0 END) as likesCount,
            SUM(CASE WHEN ur.type = 'DISLIKE' THEN 1 ELSE 0 END) as dislikesCount,
            SUM(CASE WHEN ur.type = 'SAVE' THEN 1 ELSE 0 END) as savesCount
        FROM UserRecipe ur
        WHERE ur.recipe.id = :recipeId
    """)
    RecipeUserInteractionProjection findUserInteractionsAndCounts(int userId, int recipeId);

    @Query("""
        SELECT
            ur.recipe.id as recipeId,
            SUM(CASE WHEN ur.type = 'LIKE' THEN 1 ELSE 0 END) as likesCount,
            SUM(CASE WHEN ur.type = 'DISLIKE' THEN 1 ELSE 0 END) as dislikesCount,
            SUM(CASE WHEN ur.type = 'SAVE' THEN 1 ELSE 0 END) as savesCount
        FROM UserRecipe ur
        WHERE ur.recipe.id IN :recipeIds
        GROUP BY ur.recipe.id
    """)
    List<RecipeCountsProjection> findCountsByRecipeIds(@Param("recipeIds") List<Integer> recipeIds);

    @Query("""
        SELECT ur.recipe.id as recipeId, ur.createdAt as createdAt
        FROM UserRecipe ur
        WHERE ur.user.id = :userId
        AND ur.type = :type
        AND ur.recipe.id IN :recipeIds
    """)
    List<RecipeInteractionDateProjection> findInteractionDatesByRecipeIds(@Param("userId") int userId,
                                                                          @Param("type") TypeOfInteraction type,
                                                                          @Param("recipeIds") List<Integer> recipeIds);

    @Query(value = """
        SELECT ur
        FROM UserRecipe ur
        JOIN FETCH ur.recipe r
        WHERE ur.user.id = :userId
        AND ur.type = :type
        AND r.isPublic = true
    """)
    Page<UserRecipe> findByUserIdAndTypeWithRecipe(@Param("userId") int userId,
                                                    @Param("type") TypeOfInteraction type,
                                                    Pageable pageable);
}