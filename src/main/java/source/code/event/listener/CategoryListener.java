package source.code.event.listener;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;

@Component
public class CategoryListener {
  private final CacheManager cacheManager;
  private static final String CACHE_NAME = "allCategories";
  public CategoryListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleCacheCreateEvent(CategoryCreateCacheEvent event) {
    Cache cache = cacheManager.getCache(CACHE_NAME);

    if (cache == null) {
      throw new IllegalStateException("Cache not available: " + CACHE_NAME);
    }

    cache.put(event.getCacheKey(), event.getCachedData());
  }

  @EventListener
  public void handleCacheClearEvent(CategoryClearCacheEvent event) {
    cacheManager.getCache(CACHE_NAME).evict(event.getCacheKey());
  }

}
