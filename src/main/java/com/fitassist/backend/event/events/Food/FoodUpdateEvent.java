package com.fitassist.backend.event.events.Food;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.food.Food;

@Getter
public class FoodUpdateEvent extends ApplicationEvent {

	private final Food food;

	public FoodUpdateEvent(Object source, Food food) {
		super(source);
		this.food = food;
	}

	public static FoodUpdateEvent of(Object source, Food food) {
		return new FoodUpdateEvent(source, food);
	}

}
