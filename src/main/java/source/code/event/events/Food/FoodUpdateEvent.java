package source.code.event.events.Food;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Food.Food;

@Getter
public class FoodUpdateEvent extends ApplicationEvent {
    private final Food food;

    public FoodUpdateEvent(Object source, Food food) {
        super(source);
        this.food = food;
    }
}
