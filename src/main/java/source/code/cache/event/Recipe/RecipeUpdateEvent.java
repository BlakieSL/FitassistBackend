package source.code.cache.event.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Recipe.Recipe;

@Getter
public class RecipeUpdateEvent extends ApplicationEvent {
  private final Recipe recipe;

  public RecipeUpdateEvent(Object source, Recipe recipe) {
    super(source);
    this.recipe = recipe;
  }
}
