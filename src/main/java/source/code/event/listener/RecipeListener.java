package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.model.recipe.Recipe;
import source.code.service.declaration.cache.CacheService;
import source.code.service.declaration.search.LuceneIndexService;

@Component
public class RecipeListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public RecipeListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@EventListener
	public void handleRecipeCreate(RecipeCreateEvent event) {
		Recipe recipe = event.getRecipe();

		if (recipe.getIsPublic()) {
			luceneService.addEntity(recipe);
		}
	}

	@EventListener
	public void handleRecipeUpdate(RecipeUpdateEvent event) {
		Recipe recipe = event.getRecipe();

		clearCache(recipe);
		if (recipe.getIsPublic()) {
			luceneService.updateEntity(recipe);
		}
	}

	@EventListener
	public void handleRecipeDelete(RecipeDeleteEvent event) {
		Recipe recipe = event.getRecipe();

		clearCache(recipe);
		luceneService.deleteEntity(recipe);
	}

	private void clearCache(Recipe recipe) {
		cacheService.evictCache(CacheNames.RECIPES, recipe.getId());
	}

}
