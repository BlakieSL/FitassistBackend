package com.fitassist.backend.mapper.recipe;

import com.fitassist.backend.model.recipe.RecipeCategory;
import com.fitassist.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecipeMappingContext {

	private final User user;

	private final List<RecipeCategory> categories;

	public static RecipeMappingContext forCreate(User user, List<RecipeCategory> categories) {
		return new RecipeMappingContext(user, categories);
	}

	public static RecipeMappingContext forUpdate(List<RecipeCategory> categories) {
		return new RecipeMappingContext(null, categories);
	}

}
