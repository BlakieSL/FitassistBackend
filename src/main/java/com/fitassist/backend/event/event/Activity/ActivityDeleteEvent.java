package com.fitassist.backend.event.event.Activity;

import com.fitassist.backend.model.activity.Activity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ActivityDeleteEvent extends ApplicationEvent {

	private final Activity activity;

	public ActivityDeleteEvent(Object source, Activity activity) {
		super(source);
		this.activity = activity;
	}

	public static ActivityDeleteEvent of(Object source, Activity activity) {
		return new ActivityDeleteEvent(source, activity);
	}

}
