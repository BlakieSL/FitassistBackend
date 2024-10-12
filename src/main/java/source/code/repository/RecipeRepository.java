package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Recipe.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}