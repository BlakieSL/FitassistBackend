package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Food.FoodCreateEvent;
import source.code.cache.event.Food.FoodDeleteEvent;
import source.code.cache.event.Food.FoodUpdateEvent;
import source.code.model.Food.Food;

@Component
public class FoodCacheListener {
  private final CacheManager cacheManager;

  public FoodCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleFoodCreate(FoodCreateEvent event) {
    Food food = event.getFood();
    clearCommonCache(food);
  }

  @EventListener
  public void handleFoodUpdate(FoodUpdateEvent event) {
    Food food = event.getFood();
    clearCache(food);
  }

  @EventListener
  public void handleFoodDelete(FoodDeleteEvent event) {
    Food food = event.getFood();
    clearCache(food);
  }

  private void clearCache(Food food) {
    cacheManager.getCache("foods").evict(food.getId());
    clearCommonCache(food);
  }

  private void clearCommonCache(Food food) {
    cacheManager.getCache("allFoods").clear();
    cacheManager.getCache("foodsByCategory").evict(food.getFoodCategory().getId());
  }
}
