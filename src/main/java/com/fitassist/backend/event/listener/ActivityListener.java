package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.event.Activity.ActivityCreateEvent;
import com.fitassist.backend.event.event.Activity.ActivityDeleteEvent;
import com.fitassist.backend.event.event.Activity.ActivityUpdateEvent;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ActivityListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public ActivityListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleActivityCreate(ActivityCreateEvent event) {
		Activity activity = event.getActivity();

		luceneService.addEntity(activity);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleActivityUpdate(ActivityUpdateEvent event) {
		Activity activity = event.getActivity();

		clearCache(activity);
		luceneService.updateEntity(activity);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleActivityDelete(ActivityDeleteEvent event) {
		Activity activity = event.getActivity();

		clearCache(activity);
		luceneService.deleteEntity(activity);
	}

	public void clearCache(Activity activity) {
		cacheService.evictCache(CacheNames.ACTIVITIES, activity.getId());
	}

}
