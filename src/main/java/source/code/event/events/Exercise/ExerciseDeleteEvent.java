package source.code.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.exercise.Exercise;

@Getter
public class ExerciseDeleteEvent extends ApplicationEvent {

	private final Exercise exercise;

	public ExerciseDeleteEvent(Object source, Exercise exercise) {
		super(source);
		this.exercise = exercise;
	}

	public static ExerciseDeleteEvent of(Object source, Exercise exercise) {
		return new ExerciseDeleteEvent(source, exercise);
	}

}
