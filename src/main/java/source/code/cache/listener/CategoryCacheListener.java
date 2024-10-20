package source.code.cache.listener;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Category.CategoryClearCacheEvent;
import source.code.cache.event.Category.CategoryCreateCacheEvent;

@Component
public class CategoryCacheListener {
  private final CacheManager cacheManager;

  public CategoryCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleCacheCreateEvent(CategoryCreateCacheEvent event) {
    Cache cache = cacheManager.getCache("allCategories");

    if (cache == null) {
      throw new IllegalStateException("Cache not available: allCategories");
    }

    cache.put(event.getCacheKey(), event.getCachedData());
    System.out.println("Cache created for key: " + event.getCacheKey());
  }

  @EventListener
  public void handleCacheClearEvent(CategoryClearCacheEvent event) {
    cacheManager.getCache("allCategories").evict(event.getCacheKey());
  }

}
