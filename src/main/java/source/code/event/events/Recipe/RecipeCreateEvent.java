package source.code.event.events.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Recipe.Recipe;

@Getter
public class RecipeCreateEvent extends ApplicationEvent {
  private final Recipe recipe;
  public RecipeCreateEvent(Object source, Recipe recipe) {
    super(source);
    this.recipe = recipe;
  }
}
