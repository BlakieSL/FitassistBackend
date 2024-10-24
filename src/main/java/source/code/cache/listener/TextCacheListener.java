package source.code.cache.listener;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Category.CategoryClearCacheEvent;
import source.code.cache.event.Text.TextCreateCacheEvent;

@Component
public class TextCacheListener {
  private final CacheManager cacheManager;
  private static final String CACHE_NAME = "allTextByParent";

  public TextCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleCacheCreateEvent(TextCreateCacheEvent event) {
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
