package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.text.RecipeInstruction;

public interface RecipeInstructionRepository extends JpaRepository<RecipeInstruction, Integer> {

	List<RecipeInstruction> getAllByRecipeId(int recipeId);

}
