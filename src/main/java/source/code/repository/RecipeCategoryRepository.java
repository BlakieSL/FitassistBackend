package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.recipe.RecipeCategory;

import java.util.List;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {

	boolean existsByIdAndRecipeCategoryAssociationsIsNotEmpty(Integer id);

	List<RecipeCategory> findAllByIdIn(List<Integer> ids);

}
