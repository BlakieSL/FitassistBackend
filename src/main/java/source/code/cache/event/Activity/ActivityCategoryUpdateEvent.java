package source.code.cache.event.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Activity.ActivityCategory;

@Getter
public class ActivityCategoryUpdateEvent extends ApplicationEvent {
  public ActivityCategoryUpdateEvent(Object source) {
    super(source);
  }
}