package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.events.Activity.ActivityCreateEvent;
import com.fitassist.backend.event.events.Activity.ActivityDeleteEvent;
import com.fitassist.backend.event.events.Activity.ActivityUpdateEvent;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public ActivityListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@EventListener
	public void handleActivityCreate(ActivityCreateEvent event) {
		Activity activity = event.getActivity();

		luceneService.addEntity(activity);
	}

	@EventListener
	public void handleActivityUpdate(ActivityUpdateEvent event) {
		Activity activity = event.getActivity();

		clearCache(activity);
		luceneService.updateEntity(activity);
	}

	@EventListener
	public void handleActivityDelete(ActivityDeleteEvent event) {
		Activity activity = event.getActivity();

		clearCache(activity);
		luceneService.deleteEntity(activity);
	}

	public void clearCache(Activity activity) {
		cacheService.evictCache(CacheNames.ACTIVITIES, activity.getId());
	}

}
