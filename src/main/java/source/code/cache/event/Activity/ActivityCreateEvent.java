package source.code.cache.event.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.Activity.ActivityCreateDto;
import source.code.model.Activity.Activity;

@Getter
public class ActivityCreateEvent extends ApplicationEvent {
  private final Activity activity;

  public ActivityCreateEvent(Object source, Activity activity) {
    super(source);
    this.activity = activity;
  }
}
