package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Activity.ActivityDeleteEvent;
import source.code.event.events.Activity.ActivityUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.model.activity.Activity;
import source.code.service.declaration.cache.CacheService;
import source.code.service.declaration.search.LuceneIndexService;

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
