package com.fitassist.backend.event.events.Food;

import com.fitassist.backend.model.food.Food;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FoodDeleteEvent extends ApplicationEvent {

	private final Food food;

	public FoodDeleteEvent(Object source, Food food) {
		super(source);
		this.food = food;
	}

	public static FoodDeleteEvent of(Object source, Food food) {
		return new FoodDeleteEvent(source, food);
	}

}
