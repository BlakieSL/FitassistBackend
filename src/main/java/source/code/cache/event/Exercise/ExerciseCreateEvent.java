package source.code.cache.event.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.ExerciseCreateDto;

@Getter
public class ExerciseCreateEvent extends ApplicationEvent {
  private final ExerciseCreateDto exerciseCreateDto;
  public ExerciseCreateEvent(Object source, ExerciseCreateDto exerciseCreateDto) {
    super(source);
    this.exerciseCreateDto = exerciseCreateDto;
  }
}
