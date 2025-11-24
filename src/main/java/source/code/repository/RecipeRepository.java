package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.*;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @Modifying
    @Query("UPDATE Recipe r SET r.views = r.views + 1 WHERE r.id = :recipeId")
    void incrementViews(@Param("recipeId") Integer recipeId);

    @EntityGraph(value = "Recipe.withoutAssociations")
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true")
    List<Recipe> findAllWithoutAssociations();

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
             null,
             CAST((SELECT COUNT(ur1) FROM UserRecipe ur1 WHERE ur1.recipe.id = r.id AND ur1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'SAVE') AS int),
             r.views,
             CAST((SELECT COUNT(rf2) FROM RecipeFood rf2 WHERE rf2.recipe.id = r.id) AS int),
             null,
             r.createdAt,
             null)
      FROM Recipe r
      JOIN r.recipeFoods rf
      WHERE rf.food.id = :foodId
      ORDER BY r.createdAt DESC
    """)
    List<RecipeSummaryDto> findRecipeSummariesByFoodId(@Param("foodId") int foodId);

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
             null,
             CAST((SELECT COUNT(ur1) FROM UserRecipe ur1 WHERE ur1.recipe.id = r.id AND ur1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'SAVE') AS int),
             r.views,
             CAST((SELECT COUNT(rf) FROM RecipeFood rf WHERE rf.recipe.id = r.id) AS int),
             null,
             r.createdAt,
             CASE WHEN :fetchByInteraction = true THEN ur.createdAt ELSE null END)
      FROM Recipe r
      JOIN r.user u
      LEFT JOIN UserRecipe ur ON ur.recipe.id = r.id AND ur.user.id = :userId AND (:type IS NULL OR ur.type = :type)
      WHERE (:fetchByInteraction = false AND
             (((:isOwnProfile IS NULL OR :isOwnProfile = false) AND (r.isPublic = true AND r.user.id = :userId)) OR
              (:isOwnProfile = true AND r.user.id = :userId))) OR
            (:fetchByInteraction = true AND ur.id IS NOT NULL AND r.isPublic = true)
      ORDER BY CASE WHEN :fetchByInteraction = true THEN ur.createdAt ELSE r.createdAt END DESC
    """)
    List<RecipeSummaryDto> findRecipeSummaryUnified(@Param("userId") int userId,
                                                     @Param("type") TypeOfInteraction type,
                                                     @Param("fetchByInteraction") boolean fetchByInteraction,
                                                     @Param("isOwnProfile") Boolean isOwnProfile);

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
             null,
             CAST((SELECT COUNT(ur1) FROM UserRecipe ur1 WHERE ur1.recipe.id = r.id AND ur1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'SAVE') AS int),
             r.views,
             CAST((SELECT COUNT(rf) FROM RecipeFood rf WHERE rf.recipe.id = r.id) AS int),
             null,
             r.createdAt,
             null)
      FROM Recipe r
      WHERE r.id IN :recipeIds
      ORDER BY r.createdAt DESC
    """)
    List<RecipeSummaryDto> findRecipeSummariesByIds(@Param("recipeIds") List<Integer> recipeIds);

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
             null,
             CAST((SELECT COUNT(ur1) FROM UserRecipe ur1 WHERE ur1.recipe.id = r.id AND ur1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(ur2) FROM UserRecipe ur2 WHERE ur2.recipe.id = r.id AND ur2.type = 'SAVE') AS int),
             r.views,
             CAST((SELECT COUNT(rf) FROM RecipeFood rf WHERE rf.recipe.id = r.id) AS int),
             null,
             r.createdAt,
             null)
      FROM Recipe r
      WHERE (:isPrivate IS NULL AND r.isPublic = true) OR
            (:isPrivate = false AND r.isPublic = true) OR
            (:isPrivate = true AND r.user.id = :userId)
      ORDER BY r.createdAt DESC
    """)
    List<RecipeSummaryDto> findAllRecipeSummaries(@Param("isPrivate") Boolean isPrivate, @Param("userId") int userId);
}