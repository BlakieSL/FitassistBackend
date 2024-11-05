package source.code.event.events.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.plan.Plan;

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
