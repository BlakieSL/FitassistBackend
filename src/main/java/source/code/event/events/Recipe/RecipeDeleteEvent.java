package source.code.event.events.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.recipe.Recipe;

@Getter
public class RecipeDeleteEvent extends ApplicationEvent {
    private final Recipe recipe;

    public RecipeDeleteEvent(Object source, Recipe recipe) {
        super(source);
        this.recipe = recipe;
    }
}
