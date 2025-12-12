package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.EntityCountsProjection;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserRecipe;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Integer> {
    Optional<UserRecipe> findByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    boolean existsByUserIdAndRecipeIdAndType(int userId, int recipeId, TypeOfInteraction type);

    @Query("""
        SELECT
            r.id as entityId,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'LIKE' THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'DISLIKE' THEN 1 ELSE 0 END) as isDisliked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'SAVE' THEN 1 ELSE 0 END) as isSaved,
            COALESCE(SUM(CASE WHEN ur.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
            COALESCE(SUM(CASE WHEN ur.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
            COALESCE(SUM(CASE WHEN ur.type = 'SAVE' THEN 1 ELSE 0 END), 0) as savesCount
        FROM Recipe r
        LEFT JOIN UserRecipe ur ON ur.recipe.id = r.id
        WHERE r.id = :recipeId
        GROUP BY r.id
    """)
    EntityCountsProjection findCountsByRecipeId(@Param("userId") int userId,
                                                @Param("recipeId") int recipeId);

    @Query("""
        SELECT
            r.id as entityId,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'LIKE' THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'DISLIKE' THEN 1 ELSE 0 END) as isDisliked,
            MAX(CASE WHEN ur.user.id = :userId AND ur.type = 'SAVE' THEN 1 ELSE 0 END) as isSaved,
            COALESCE(SUM(CASE WHEN ur.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
            COALESCE(SUM(CASE WHEN ur.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
            COALESCE(SUM(CASE WHEN ur.type = 'SAVE' THEN 1 ELSE 0 END), 0) as savesCount
        FROM Recipe r
        LEFT JOIN UserRecipe ur ON ur.recipe.id = r.id
        WHERE r.id IN :recipeIds
        GROUP BY r.id
    """)
    List<EntityCountsProjection> findCountsByRecipeIds(@Param("userId") int userId,
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