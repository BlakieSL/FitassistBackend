package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.text.RecipeInstruction;

import java.util.List;

public interface RecipeInstructionRepository extends JpaRepository<RecipeInstruction, Integer> {

	List<RecipeInstruction> getAllByRecipeId(int recipeId);

}
