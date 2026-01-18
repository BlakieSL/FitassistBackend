package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.events.Recipe.RecipeCreateEvent;
import com.fitassist.backend.event.events.Recipe.RecipeDeleteEvent;
import com.fitassist.backend.event.events.Recipe.RecipeUpdateEvent;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RecipeListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public RecipeListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRecipeCreate(RecipeCreateEvent event) {
		Recipe recipe = event.getRecipe();

		if (recipe.getIsPublic()) {
			luceneService.addEntity(recipe);
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRecipeUpdate(RecipeUpdateEvent event) {
		Recipe recipe = event.getRecipe();

		clearCache(recipe);
		if (recipe.getIsPublic()) {
			luceneService.updateEntity(recipe);
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRecipeDelete(RecipeDeleteEvent event) {
		Recipe recipe = event.getRecipe();

		clearCache(recipe);
		luceneService.deleteEntity(recipe);
	}

	private void clearCache(Recipe recipe) {
		cacheService.evictCache(CacheNames.RECIPES, recipe.getId());
	}

}
