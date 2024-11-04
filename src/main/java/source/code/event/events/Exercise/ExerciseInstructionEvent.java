package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.Text.ExerciseInstruction;

@Getter
public class ExerciseInstructionEvent extends ApplicationEvent {
    private final ExerciseInstruction instruction;

    public ExerciseInstructionEvent(Object source, ExerciseInstruction instruction) {
        super(source);
        this.instruction = instruction;
    }
}
