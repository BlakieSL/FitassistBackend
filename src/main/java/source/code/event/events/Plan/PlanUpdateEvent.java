package source.code.event.events.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.plan.Plan;

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
