package com.fitassist.backend.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe_category_association")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCategoryAssociation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	private Recipe recipe;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "recipe_category_id", nullable = false)
	private RecipeCategory recipeCategory;

	public static RecipeCategoryAssociation createWithRecipeAndCategory(Recipe recipe, RecipeCategory recipeCategory) {
		RecipeCategoryAssociation recipeCategoryAssociation = new RecipeCategoryAssociation();
		recipeCategoryAssociation.setRecipe(recipe);
		recipeCategoryAssociation.setRecipeCategory(recipeCategory);

		return recipeCategoryAssociation;
	}

}
