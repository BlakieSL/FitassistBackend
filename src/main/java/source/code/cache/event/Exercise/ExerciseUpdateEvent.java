package source.code.cache.event.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Exercise.Exercise;

@Getter
public class ExerciseUpdateEvent extends ApplicationEvent {
  private final Exercise exercise;

  public ExerciseUpdateEvent(Object source, Exercise exercise) {
    super(source);
    this.exercise = exercise;
  }
}
