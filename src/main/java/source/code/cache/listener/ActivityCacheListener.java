package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Activity.ActivityCreateEvent;
import source.code.cache.event.Activity.ActivityDeleteEvent;
import source.code.cache.event.Activity.ActivityUpdateEvent;
import source.code.model.Activity.Activity;

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

  @EventListener
  public void handleActivityUpdate(ActivityUpdateEvent event) {
    Activity activity = event.getActivity();
    clearCache(activity);
  }

  @EventListener
  public void handleActivityDelete(ActivityDeleteEvent event) {
    Activity activity = event.getActivity();
    clearCache(activity);
  }

  public void clearCache(Activity activity) {
    cacheManager.getCache("activities").evict(activity.getId());
    cacheManager.getCache("allActivities").clear();
    cacheManager.getCache("activitiesByCategory").evict(activity.getActivityCategory().getId());
  }
}
