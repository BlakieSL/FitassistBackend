package com.fitassist.backend.event.events.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.activity.Activity;

@Getter
public class ActivityUpdateEvent extends ApplicationEvent {

	private final Activity activity;

	public ActivityUpdateEvent(Object source, Activity activity) {
		super(source);
		this.activity = activity;
	}

	public static ActivityUpdateEvent of(Object source, Activity activity) {
		return new ActivityUpdateEvent(source, activity);
	}

}
