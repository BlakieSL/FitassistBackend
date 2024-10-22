package source.code.cache.event.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Exercise.ExerciseInstruction;

@Getter
public class ExerciseInstructionEvent extends ApplicationEvent {
  private final ExerciseInstruction instruction;
  public ExerciseInstructionEvent(Object source, ExerciseInstruction instruction) {
    super(source);
    this.instruction = instruction;
  }
}
