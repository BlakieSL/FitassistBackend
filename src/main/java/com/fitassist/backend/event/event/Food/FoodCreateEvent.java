package com.fitassist.backend.event.event.Food;

import com.fitassist.backend.model.food.Food;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FoodCreateEvent extends ApplicationEvent {

	private final Food food;

	public FoodCreateEvent(Object source, Food food) {
		super(source);
		this.food = food;
	}

	public static FoodCreateEvent of(Object source, Food food) {
		return new FoodCreateEvent(source, food);
	}

}
