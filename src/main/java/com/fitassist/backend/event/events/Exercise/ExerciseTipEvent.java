package com.fitassist.backend.event.events.Exercise;

import com.fitassist.backend.model.text.ExerciseTip;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ExerciseTipEvent extends ApplicationEvent {

	private final ExerciseTip tip;

	public ExerciseTipEvent(Object source, ExerciseTip tip) {
		super(source);
		this.tip = tip;
	}

}
