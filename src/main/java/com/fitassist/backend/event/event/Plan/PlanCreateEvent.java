package com.fitassist.backend.event.event.Plan;

import com.fitassist.backend.model.plan.Plan;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlanCreateEvent extends ApplicationEvent {

	private final Plan plan;

	public PlanCreateEvent(Object source, Plan plan) {
		super(source);
		this.plan = plan;
	}

	public static PlanCreateEvent of(Object source, Plan plan) {
		return new PlanCreateEvent(source, plan);
	}

}
