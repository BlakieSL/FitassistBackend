package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @Override
    @EntityGraph(attributePaths = {"user", "mediaList", "recipeCategoryAssociations.recipeCategory"})
    @NotNull
    List<Recipe> findAll(Specification<Recipe> spec);

    @Modifying
    @Query("UPDATE Recipe r SET r.views = r.views + 1 WHERE r.id = :recipeId")
    void incrementViews(@Param("recipeId") Integer recipeId);

    @EntityGraph(value = "Recipe.withoutAssociations")
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true")
    List<Recipe> findAllWithoutAssociations();

    @Query("""
      SELECT r
      FROM Recipe r
      LEFT JOIN FETCH r.recipeCategoryAssociations rca
      LEFT JOIN FETCH rca.recipeCategory rc
      LEFT JOIN FETCH r.recipeFoods rf
      LEFT JOIN FETCH rf.food f
      LEFT JOIN FETCH f.foodCategory
      LEFT JOIN FETCH r.recipeInstructions ri
      LEFT JOIN FETCH r.mediaList m
      LEFT JOIN FETCH r.user u
      WHERE r.id = :recipeId
    """)
    Optional<Recipe> findByIdWithDetails(@Param("recipeId") int recipeId);

    @Query("""
      SELECT DISTINCT r
      FROM Recipe r
      LEFT JOIN FETCH r.recipeCategoryAssociations rca
      LEFT JOIN FETCH rca.recipeCategory
      LEFT JOIN FETCH r.mediaList
      LEFT JOIN FETCH r.user u
      WHERE (:isPrivate IS NULL AND r.isPublic = true) OR
            (:isPrivate = false AND r.isPublic = true) OR
            (:isPrivate = true AND r.user.id = :userId)
      ORDER BY r.createdAt DESC
    """)
    List<Recipe> findAllWithDetails(@Param("isPrivate") Boolean isPrivate, @Param("userId") int userId);

    @Query("""
      SELECT DISTINCT r
      FROM Recipe r
      LEFT JOIN FETCH r.recipeCategoryAssociations rca
      LEFT JOIN FETCH rca.recipeCategory
      LEFT JOIN FETCH r.mediaList
      LEFT JOIN FETCH r.user u
      JOIN r.recipeFoods rf
      WHERE rf.food.id = :foodId
      ORDER BY r.createdAt DESC
    """)
    List<Recipe> findAllWithDetailsByFoodId(@Param("foodId") int foodId);

    @Query(value = """
      SELECT DISTINCT r
      FROM Recipe r
      LEFT JOIN FETCH r.recipeCategoryAssociations rca
      LEFT JOIN FETCH rca.recipeCategory
      LEFT JOIN FETCH r.mediaList
      LEFT JOIN FETCH r.user u
      WHERE ((:isOwnProfile = false OR :isOwnProfile IS NULL) AND r.isPublic = true AND r.user.id = :userId) OR
            (:isOwnProfile = true AND r.user.id = :userId)
    """)
    Page<Recipe> findCreatedByUserWithDetails(@Param("userId") int userId, @Param("isOwnProfile") boolean isOwnProfile, Pageable pageable);

    @Query(value = """
      SELECT DISTINCT r
      FROM Recipe r
      LEFT JOIN FETCH r.recipeCategoryAssociations rca
      LEFT JOIN FETCH rca.recipeCategory
      LEFT JOIN FETCH r.mediaList
      LEFT JOIN FETCH r.user u
      WHERE r.id IN :recipeIds
    """)
    List<Recipe> findByIdsWithDetails(@Param("recipeIds") List<Integer> recipeIds);
}