package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Food.FoodCreateEvent;

@Component
public class FoodCacheListener {
  private final CacheManager cacheManager;

  public FoodCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleFoodCreate(FoodCreateEvent event) {
    cacheManager.getCache("allFoods").clear();

    int categoryId = event.getFoodCreateDto().getCategoryId();
    cacheManager.getCache("foodsByCategory").evict(categoryId);
  }
}
