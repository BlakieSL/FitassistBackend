package com.fitassist.backend.event.event.Recipe;

import com.fitassist.backend.model.recipe.Recipe;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecipeCreateEvent extends ApplicationEvent {

	private final Recipe recipe;

	public RecipeCreateEvent(Object source, Recipe recipe) {
		super(source);
		this.recipe = recipe;
	}

	public static RecipeCreateEvent of(Object source, Recipe recipe) {
		return new RecipeCreateEvent(source, recipe);
	}

}
