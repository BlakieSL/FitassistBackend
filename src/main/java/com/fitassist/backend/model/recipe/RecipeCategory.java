package com.fitassist.backend.model.recipe;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe_category")
@Getter
@Setter
public class RecipeCategory extends CategoryBase {

	@OneToMany(mappedBy = "recipeCategory")
	private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new HashSet<>();

}
