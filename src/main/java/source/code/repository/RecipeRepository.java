package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.model.recipe.Recipe;

import java.util.List;

public interface RecipeRepository
        extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @EntityGraph(value = "Recipe.withoutAssociations")
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true")
    List<Recipe> findAllWithoutAssociations();

    @EntityGraph(attributePaths = {"user", "recipeCategoryAssociations.recipeCategory"})
    @Query("SELECT r FROM Recipe r WHERE " +
            "(:isPrivate IS NULL AND r.isPublic = true) OR " +
            "(:isPrivate = false AND r.isPublic = true) OR " +
            "(:isPrivate = true AND r.user.id = :userId)")
    List<Recipe> findAllWithAssociations(
            @Param("isPrivate") Boolean isPrivate,
            @Param("userId") int userId
    );

    @Query("""
        SELECT r FROM Recipe r
        WHERE ((:isPrivate IS NULL OR :isPrivate = false) AND (r.isPublic = true AND r.user.id = :userId)) OR
              (:isPrivate = true AND r.user.id = :userId)
""")
    List<Recipe> findAllByUser_Id(@Param("isPrivate") Boolean isPrivate,
                                  @Param("userId") int userId);

    @Query("""
      SELECT new source.code.dto.response.recipe.RecipeSummaryDto(
             r.id,
             r.name,
             r.description,
             r.isPublic,
             r.user.username,
             r.user.id,
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = r.user.id
              AND m.parentType = 'USER'
              ORDER BY m.id ASC
              LIMIT 1),
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = r.id
              AND m.parentType = 'RECIPE'
              ORDER BY m.id ASC
              LIMIT 1),
             null,
             CAST((SELECT COUNT(ur1) FROM UserRecipe ur1 WHERE ur1.recipe.id = r.id AND ur1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'SAVE') AS int),
             r.views,
             CAST((SELECT COUNT(rf) FROM RecipeFood rf WHERE rf.recipe.id = r.id) AS int),
             r.createdAt,
             null)
      FROM Recipe r
      WHERE ((:isOwnProfile IS NULL OR :isOwnProfile = false) AND (r.isPublic = true AND r.user.id = :userId)) OR
            (:isOwnProfile = true AND r.user.id = :userId)
      ORDER BY r.createdAt DESC
    """)
    List<RecipeSummaryDto> findSummaryByUserId(@Param("isOwnProfile") Boolean isOwnProfile,
                                               @Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE Recipe r SET r.views = r.views + 1 WHERE r.id = :recipeId")
    void incrementViews(@Param("recipeId") Integer recipeId);
}