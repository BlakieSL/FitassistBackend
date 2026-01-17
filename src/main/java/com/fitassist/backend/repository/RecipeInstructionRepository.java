package com.fitassist.backend.repository;

import com.fitassist.backend.model.text.RecipeInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeInstructionRepository extends JpaRepository<RecipeInstruction, Integer> {

	List<RecipeInstruction> getAllByRecipeId(int recipeId);

}
