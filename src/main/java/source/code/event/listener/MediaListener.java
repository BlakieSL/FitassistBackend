package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Media.MediaDeleteEvent;
import source.code.event.events.Media.MediaUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.model.media.Media;
import source.code.service.declaration.cache.CacheService;

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
		cacheService.evictCache(cacheName, media.getParentId());
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
