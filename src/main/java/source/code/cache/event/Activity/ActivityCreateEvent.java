package source.code.cache.event.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.Activity.ActivityCreateDto;

@Getter
public class ActivityCreateEvent extends ApplicationEvent {
  private final ActivityCreateDto activityCreateDto;

  public ActivityCreateEvent(Object source, ActivityCreateDto activityCreateDto) {
    super(source);
    this.activityCreateDto = activityCreateDto;
  }
}
