package com.fitassist.backend.repository;

import com.fitassist.backend.model.recipe.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {

	boolean existsByIdAndRecipeCategoryAssociationsIsNotEmpty(Integer id);

	List<RecipeCategory> findAllByIdIn(List<Integer> ids);

}
