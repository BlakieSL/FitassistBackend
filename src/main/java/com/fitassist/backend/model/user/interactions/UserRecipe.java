package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_recipe")
@Getter
@Setter
public class UserRecipe extends UserInteractionWithType {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "recipie_id", nullable = false)
	private Recipe recipe;

	public static UserRecipe of(User user, Recipe recipe, TypeOfInteraction type) {
		UserRecipe userRecipe = new UserRecipe();
		userRecipe.setUser(user);
		userRecipe.setRecipe(recipe);
		userRecipe.setType(type);
		return userRecipe;
	}

}
