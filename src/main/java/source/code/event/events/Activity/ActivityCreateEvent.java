package source.code.event.events.Activity;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.activity.Activity;

@Getter
public class ActivityCreateEvent extends ApplicationEvent {

	private final Activity activity;

	public ActivityCreateEvent(Object source, Activity activity) {
		super(source);
		this.activity = activity;
	}

	public static ActivityCreateEvent of(Object source, Activity activity) {
		return new ActivityCreateEvent(source, activity);
	}

}
