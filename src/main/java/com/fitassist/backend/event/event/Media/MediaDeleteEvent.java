package com.fitassist.backend.event.event.Media;

import com.fitassist.backend.model.media.Media;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

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
