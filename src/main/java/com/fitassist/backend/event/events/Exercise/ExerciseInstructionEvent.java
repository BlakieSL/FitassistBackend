package com.fitassist.backend.event.events.Exercise;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.text.ExerciseInstruction;

@Getter
public class ExerciseInstructionEvent extends ApplicationEvent {

	private final ExerciseInstruction instruction;

	public ExerciseInstructionEvent(Object source, ExerciseInstruction instruction) {
		super(source);
		this.instruction = instruction;
	}

}
