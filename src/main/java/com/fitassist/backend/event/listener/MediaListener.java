package com.fitassist.backend.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.fitassist.backend.event.events.Media.MediaDeleteEvent;
import com.fitassist.backend.event.events.Media.MediaUpdateEvent;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.service.declaration.cache.CacheService;

@Component
public class MediaListener {

	private final CacheService cacheService;

	public MediaListener(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@EventListener
	public void handleMediaUpdate(MediaUpdateEvent event) {
		Media media = event.getMedia();
		invalidateParentCache(media);
	}

	@EventListener
	public void handleMediaDelete(MediaDeleteEvent event) {
		Media media = event.getMedia();
		invalidateParentCache(media);
	}

	private void invalidateParentCache(Media media) {
		String cacheName = getCacheNameForParentType(media.getParentType());
		if (cacheName != null) {
			cacheService.evictCache(cacheName, media.getParentId());
		}
	}

	private String getCacheNameForParentType(MediaConnectedEntity parentType) {
		return switch (parentType) {
			case FOOD -> CacheNames.FOODS;
			case ACTIVITY -> CacheNames.ACTIVITIES;
			case EXERCISE -> CacheNames.EXERCISES;
			case RECIPE -> CacheNames.RECIPES;
			case PLAN -> CacheNames.PLANS;
			default -> null;
		};
	}

}
