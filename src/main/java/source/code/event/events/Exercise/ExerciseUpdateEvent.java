package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.exercise.Exercise;

@Getter
public class ExerciseUpdateEvent extends ApplicationEvent {
    private final Exercise exercise;

    public ExerciseUpdateEvent(Object source, Exercise exercise) {
        super(source);
        this.exercise = exercise;
    }

    public static ExerciseUpdateEvent of(Object source, Exercise exercise) {
        return new ExerciseUpdateEvent(source, exercise);
    }
}
