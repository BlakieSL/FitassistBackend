package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Exercise.Exercise;

@Getter
public class ExerciseCreateEvent extends ApplicationEvent {
  private final Exercise exercise;
  public ExerciseCreateEvent(Object source, Exercise exercise) {
    super(source);
    this.exercise = exercise;
  }
}
