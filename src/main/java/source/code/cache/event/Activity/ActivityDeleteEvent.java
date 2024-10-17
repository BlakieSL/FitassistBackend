package source.code.cache.event.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Activity.Activity;

@Getter
public class ActivityDeleteEvent extends ApplicationEvent {
  private final Activity activity;
  public ActivityDeleteEvent(Object source, Activity activity) {
    super(source);
    this.activity = activity;
  }
}
