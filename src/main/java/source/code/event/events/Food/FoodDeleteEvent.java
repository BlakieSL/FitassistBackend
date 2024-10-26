package source.code.event.events.Food;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Food.Food;

@Getter
public class FoodDeleteEvent extends ApplicationEvent {
  private final Food food;

  public FoodDeleteEvent(Object source, Food food) {
    super(source);
    this.food = food;
  }
}
