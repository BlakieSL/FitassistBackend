package com.fitassist.backend.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.exercise.Exercise;

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
