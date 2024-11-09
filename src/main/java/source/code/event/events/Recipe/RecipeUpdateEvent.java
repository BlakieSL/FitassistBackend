package source.code.event.events.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.recipe.Recipe;

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
