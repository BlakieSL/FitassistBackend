package com.fitassist.backend.event.events.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.plan.Plan;

@Getter
public class PlanUpdateEvent extends ApplicationEvent {

	private final Plan plan;

	public PlanUpdateEvent(Object source, Plan plan) {
		super(source);
		this.plan = plan;
	}

	public static PlanUpdateEvent of(Object source, Plan plan) {
		return new PlanUpdateEvent(source, plan);
	}

}
