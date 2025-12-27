package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.recipe.RecipeCategory;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {

	boolean existsByIdAndRecipeCategoryAssociationsIsNotEmpty(Integer id);

}
