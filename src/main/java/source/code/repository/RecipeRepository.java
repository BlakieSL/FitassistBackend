package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.recipe.Recipe;

import java.util.List;

public interface RecipeRepository
        extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @EntityGraph(value = "Recipe.withoutAssociations")
    @Query("SELECT r FROM Recipe r")
    List<Recipe> findAllWithoutAssociations();

    @EntityGraph(attributePaths = {"user", "recipeCategoryAssociations.recipeCategory"})
    @Query("SELECT r FROM Recipe r")
    List<Recipe> findAllWithAssociations();
}