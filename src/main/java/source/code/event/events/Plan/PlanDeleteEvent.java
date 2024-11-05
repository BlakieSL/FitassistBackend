package source.code.event.events.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.plan.Plan;

@Getter
public class PlanDeleteEvent extends ApplicationEvent {
    private final Plan plan;

    public PlanDeleteEvent(Object source, Plan plan) {
        super(source);
        this.plan = plan;
    }

    public static PlanDeleteEvent of(Object source, Plan plan) {
        return new PlanDeleteEvent(source, plan);
    }
}
