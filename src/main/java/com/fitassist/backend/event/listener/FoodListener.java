package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.events.Food.FoodCreateEvent;
import com.fitassist.backend.event.events.Food.FoodDeleteEvent;
import com.fitassist.backend.event.events.Food.FoodUpdateEvent;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FoodListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public FoodListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleFoodCreate(FoodCreateEvent event) {
		Food food = event.getFood();

		luceneService.addEntity(food);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleFoodUpdate(FoodUpdateEvent event) {
		Food food = event.getFood();

		clearCache(food);
		luceneService.updateEntity(food);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleFoodDelete(FoodDeleteEvent event) {
		Food food = event.getFood();

		clearCache(food);
		luceneService.deleteEntity(food);
	}

	private void clearCache(Food food) {
		cacheService.evictCache(CacheNames.FOODS, food.getId());
	}

}
