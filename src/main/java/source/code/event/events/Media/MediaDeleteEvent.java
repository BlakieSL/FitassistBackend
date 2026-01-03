package source.code.event.events.Media;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.media.Media;

@Getter
public class MediaDeleteEvent extends ApplicationEvent {

	private final Media media;

	public MediaDeleteEvent(Object source, Media media) {
		super(source);
		this.media = media;
	}

	public static MediaDeleteEvent of(Object source, Media media) {
		return new MediaDeleteEvent(source, media);
	}

}