package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.recipe.RecipeCategory;

import java.util.List;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Integer> {

	boolean existsByIdAndRecipeCategoryAssociationsIsNotEmpty(Integer id);

	List<RecipeCategory> findAllByIdIn(List<Integer> ids);

}
