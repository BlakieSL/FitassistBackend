package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.exercise.Exercise;

@Getter
public class ExerciseCreateEvent extends ApplicationEvent {

	private final Exercise exercise;

	public ExerciseCreateEvent(Object source, Exercise exercise) {
		super(source);
		this.exercise = exercise;
	}

	public static ExerciseCreateEvent of(Object source, Exercise exercise) {
		return new ExerciseCreateEvent(source, exercise);
	}

}
