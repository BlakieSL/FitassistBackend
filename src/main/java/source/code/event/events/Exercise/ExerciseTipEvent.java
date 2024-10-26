package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Text.ExerciseTip;

@Getter
public class ExerciseTipEvent extends ApplicationEvent {
  private final ExerciseTip tip;
  public ExerciseTipEvent(Object source, ExerciseTip tip) {
    super(source);
    this.tip = tip;
  }
}
