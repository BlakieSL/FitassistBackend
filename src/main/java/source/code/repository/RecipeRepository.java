package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.Exercise.Exercise;
import source.code.model.Recipe.Recipe;

import java.util.List;

public interface RecipeRepository
        extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
  @EntityGraph(value = "Recipe.withoutAssociations")
  @Query("SELECT r FROM Recipe r")
  List<Recipe> findAllWithoutAssociations();
}