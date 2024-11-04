package source.code.event.events.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Plan.Plan;

@Getter
public class PlanUpdateEvent extends ApplicationEvent {
    private final Plan plan;

    public PlanUpdateEvent(Object source, Plan plan) {
        super(source);
        this.plan = plan;
    }
}
