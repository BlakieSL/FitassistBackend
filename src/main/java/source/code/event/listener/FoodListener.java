package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Food.FoodCreateEvent;
import source.code.event.events.Food.FoodDeleteEvent;
import source.code.event.events.Food.FoodUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.model.food.Food;
import source.code.service.declaration.cache.CacheService;
import source.code.service.declaration.search.LuceneIndexService;

@Component
public class FoodListener {
    private final CacheService cacheService;
    private final LuceneIndexService luceneService;

    public FoodListener(CacheService cacheService, LuceneIndexService luceneService) {
        this.cacheService = cacheService;
        this.luceneService = luceneService;
    }

    @EventListener
    public void handleFoodCreate(FoodCreateEvent event) {
        Food food = event.getFood();

        clearCommonCache(food);
        luceneService.addEntity(food);
    }

    @EventListener
    public void handleFoodUpdate(FoodUpdateEvent event) {
        Food food = event.getFood();

        clearCache(food);
        luceneService.updateEntity(food);
    }

    @EventListener
    public void handleFoodDelete(FoodDeleteEvent event) {
        Food food = event.getFood();

        clearCache(food);
        luceneService.deleteEntity(food);
    }

    private void clearCache(Food food) {
        cacheService.evictCache(CacheNames.FOODS, food.getId());
        clearCommonCache(food);
    }

    private void clearCommonCache(Food food) {
        cacheService.clearCache(CacheNames.ALL_FOODS);
        cacheService.evictCache(CacheNames.FOODS_BY_CATEGORY, food.getFoodCategory().getId());
    }
}
