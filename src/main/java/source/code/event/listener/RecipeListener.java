package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.service.Declaration.Cache.CacheService;
import source.code.service.Declaration.Search.LuceneIndexService;

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

    clearCommonCache(recipe);
    luceneService.addEntity(recipe);
  }

  @EventListener
  public void handleRecipeUpdate(RecipeUpdateEvent event) {
    Recipe recipe = event.getRecipe();

    clearCache(recipe);
    luceneService.updateEntity(recipe);
  }

  @EventListener
  public void handleRecipeDelete(RecipeDeleteEvent event) {
    Recipe recipe = event.getRecipe();

    clearCache(recipe);
    luceneService.deleteEntity(recipe);
  }

  private void clearCache(Recipe recipe) {
    cacheService.evictCache(CacheNames.RECIPES, recipe.getId());
    clearCommonCache(recipe);
  }

  private void clearCommonCache(Recipe recipe) {
    cacheService.clearCache(CacheNames.ALL_RECIPES);
    clearRecipesByCategoryCache(recipe);
  }

  private void clearRecipesByCategoryCache(Recipe recipe) {
    if (recipe.getRecipeCategoryAssociations() != null) {
      for (RecipeCategoryAssociation association : recipe.getRecipeCategoryAssociations()) {
        cacheService.evictCache(CacheNames.RECIPES_BY_CATEGORY, association.getRecipeCategory().getId());
      }
    }
  }
}
