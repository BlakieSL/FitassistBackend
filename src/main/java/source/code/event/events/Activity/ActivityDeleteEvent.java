package source.code.event.events.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.activity.Activity;

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
