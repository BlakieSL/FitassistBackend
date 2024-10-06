package source.code.repository;

import source.code.model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {
    Optional<RecipeCategory> findByName(String name);
}