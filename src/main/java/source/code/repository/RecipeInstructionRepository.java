package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Text.RecipeInstruction;

import java.util.List;

public interface RecipeInstructionRepository extends JpaRepository<RecipeInstruction, Integer> {
    List<RecipeInstruction> getAllByRecipeId(int recipeId);
}