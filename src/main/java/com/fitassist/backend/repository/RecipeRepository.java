package com.fitassist.backend.repository;

import com.fitassist.backend.model.recipe.Recipe;
import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

import static com.fitassist.backend.model.recipe.Recipe.GRAPH_BASE;
import static com.fitassist.backend.model.recipe.Recipe.GRAPH_SUMMARY;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {

	@Modifying
	@Query("UPDATE Recipe r SET r.views = r.views + 1 WHERE r.id = :recipeId")
	void incrementViews(@Param("recipeId") int recipeId);

	@Query("SELECT r.views FROM Recipe r WHERE r.id = :recipeId")
	Long getViews(@Param("recipeId") int recipeId);

	@EntityGraph(value = GRAPH_BASE)
	@Query("SELECT r FROM Recipe r WHERE r.isPublic = true")
	List<Recipe> findAllWithoutAssociations();

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	Page<Recipe> findAll(Specification<Recipe> spec, @NotNull Pageable pageable);

	@Query("""
			  SELECT r
			  FROM Recipe r
			  JOIN FETCH r.user
			  LEFT JOIN FETCH r.recipeCategoryAssociations rca
			  LEFT JOIN FETCH rca.recipeCategory
			  LEFT JOIN FETCH r.recipeFoods rf
			  LEFT JOIN FETCH rf.food f
			  LEFT JOIN FETCH f.foodCategory
			  LEFT JOIN FETCH r.recipeInstructions ri
			  WHERE r.id = :recipeId
			""")
	Optional<Recipe> findByIdWithDetails(@Param("recipeId") int recipeId);

	@EntityGraph(value = GRAPH_SUMMARY)
	@Query("""
			    SELECT r
			    FROM Recipe r
			    JOIN r.recipeFoods rf
			    WHERE rf.food.id = :foodId
			""")
	List<Recipe> findAllWithDetailsByFoodId(@Param("foodId") int foodId);

	@EntityGraph(value = GRAPH_SUMMARY)
	@Query("""
			    SELECT r
			    FROM Recipe r
			    WHERE r.id IN :recipeIds
			""")
	List<Recipe> findByIdsWithDetails(@Param("recipeIds") List<Integer> recipeIds);

}
