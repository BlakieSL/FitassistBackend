package source.code.cache.event.Plan;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.PlanCreateDto;

public class Plan {
  @Getter
  public static class PlanCreateEvent extends ApplicationEvent {
    private final PlanCreateDto planCreateDto;
    public PlanCreateEvent(Object source, PlanCreateDto planCreateDto) {
      super(source);
      this.planCreateDto = planCreateDto;
    }
  }
}
