package source.code.cache.event.Food;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.FoodCreateDto;

@Getter
public class FoodCreateEvent extends ApplicationEvent {
  private final FoodCreateDto foodCreateDto;
  public FoodCreateEvent(Object source, FoodCreateDto foodCreateDto) {
    super(source);
    this.foodCreateDto = foodCreateDto;
  }
}
