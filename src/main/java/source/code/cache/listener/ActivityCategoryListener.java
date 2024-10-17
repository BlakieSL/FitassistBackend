package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Activity.ActivityCategoryCreateEvent;
import source.code.cache.event.Activity.ActivityCategoryDeleteEvent;
import source.code.cache.event.Activity.ActivityCategoryUpdateEvent;
import source.code.model.Activity.ActivityCategory;

@Component
public class ActivityCategoryListener {
  private final CacheManager cacheManager;

  public ActivityCategoryListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleCategoryCreate(ActivityCategoryCreateEvent event) {
    clearCache();
  }

  @EventListener
  public void handleCategoryUpdate(ActivityCategoryUpdateEvent event) {
    clearCache();
  }

  @EventListener
  public void handleCategoryDelete(ActivityCategoryDeleteEvent event) {
    clearCache();
  }

  private void clearCache() {
    cacheManager.getCache("allActivityCategories").clear();
  }
}
