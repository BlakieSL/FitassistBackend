package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.recipe.Recipe;

import java.util.List;
import java.util.jar.JarFile;

public interface RecipeRepository
        extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @EntityGraph(value = "Recipe.withoutAssociations")
    @Query("SELECT r FROM Recipe r")
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

    List<Recipe> findAllByUser_Id(Integer userId);
}