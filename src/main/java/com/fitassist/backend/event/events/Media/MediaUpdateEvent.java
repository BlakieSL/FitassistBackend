package com.fitassist.backend.event.events.Media;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.fitassist.backend.model.media.Media;

@Getter
public class MediaUpdateEvent extends ApplicationEvent {

	private final Media media;

	public MediaUpdateEvent(Object source, Media media) {
		super(source);
		this.media = media;
	}

	public static MediaUpdateEvent of(Object source, Media media) {
		return new MediaUpdateEvent(source, media);
	}

}