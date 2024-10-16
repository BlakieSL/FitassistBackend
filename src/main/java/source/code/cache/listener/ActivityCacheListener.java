package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Activity.ActivityCreateEvent;

@Component
public class ActivityCacheListener {
  private final CacheManager cacheManager;

  public ActivityCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleActivityCreate(ActivityCreateEvent event) {
    cacheManager.getCache("allActivities").clear();

    int categoryId = event.getActivityCreateDto().getCategoryId();
    cacheManager.getCache("activitiesByCategory").evict(categoryId);
  }
}
