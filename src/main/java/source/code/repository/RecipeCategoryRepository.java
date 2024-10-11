package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.RecipeCategory;

import java.util.Optional;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {
  Optional<RecipeCategory> findByName(String name);
}