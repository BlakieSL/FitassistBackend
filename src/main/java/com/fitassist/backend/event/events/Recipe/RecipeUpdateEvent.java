package com.fitassist.backend.event.events.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.recipe.Recipe;

@Getter
public class RecipeUpdateEvent extends ApplicationEvent {

	private final Recipe recipe;

	public RecipeUpdateEvent(Object source, Recipe recipe) {
		super(source);
		this.recipe = recipe;
	}

	public static RecipeUpdateEvent of(Object source, Recipe recipe) {
		return new RecipeUpdateEvent(source, recipe);
	}

}
