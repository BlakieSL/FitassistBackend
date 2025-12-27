package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.service.declaration.cache.CacheService;

@Component
public class CategoryListener {

	private final CacheService cacheService;

	public CategoryListener(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@EventListener
	public void handleCacheCreateEvent(CategoryCreateCacheEvent event) {
		cacheService.putCache(CacheNames.ALL_CATEGORIES, event.getCacheKey(), event.getCachedData());
	}

	@EventListener
	public void handleCacheClearEvent(CategoryClearCacheEvent event) {
		cacheService.evictCache(CacheNames.ALL_CATEGORIES, event.getCacheKey());
	}

}
