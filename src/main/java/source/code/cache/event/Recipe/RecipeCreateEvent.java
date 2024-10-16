package source.code.cache.event.Recipe;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.RecipeCreateDto;

@Getter
public class RecipeCreateEvent extends ApplicationEvent {
  private final RecipeCreateDto recipeCreateDto;
  public RecipeCreateEvent(Object source, RecipeCreateDto recipeCreateDto) {
    super(source);
    this.recipeCreateDto = recipeCreateDto;
  }
}
