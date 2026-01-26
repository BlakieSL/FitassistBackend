package com.fitassist.backend.event.event.Recipe;

import com.fitassist.backend.model.recipe.Recipe;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecipeDeleteEvent extends ApplicationEvent {

	private final Recipe recipe;

	public RecipeDeleteEvent(Object source, Recipe recipe) {
		super(source);
		this.recipe = recipe;
	}

	public static RecipeDeleteEvent of(Object source, Recipe recipe) {
		return new RecipeDeleteEvent(source, recipe);
	}

}
