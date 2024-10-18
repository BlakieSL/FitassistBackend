package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Recipe.RecipeCreateEvent;
import source.code.cache.event.Recipe.RecipeDeleteEvent;
import source.code.cache.event.Recipe.RecipeUpdateEvent;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategoryAssociation;

@Component
public class RecipeCacheListener {
  private final CacheManager cacheManager;

  public RecipeCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleRecipeCreate(RecipeCreateEvent event) {
    Recipe recipe = event.getRecipe();
    clearCommonCache(recipe);
  }

  @EventListener
  public void handleRecipeUpdate(RecipeUpdateEvent event) {
    Recipe recipe = event.getRecipe();
    clearCache(recipe);
  }

  @EventListener
  public void handleRecipeDelete(RecipeDeleteEvent event) {
    Recipe recipe = event.getRecipe();
    clearCache(recipe);
  }

  private void clearCache(Recipe recipe) {
    cacheManager.getCache("recipes").evict(recipe.getId());
    clearCommonCache(recipe);
  }

  private void clearCommonCache(Recipe recipe) {
    cacheManager.getCache("allRecipes").clear();
    clearRecipesByCategoryCache(recipe);
  }

  private void clearRecipesByCategoryCache(Recipe recipe) {
    if(recipe.getRecipeCategoryAssociations() != null) {
      for(RecipeCategoryAssociation association : recipe.getRecipeCategoryAssociations()) {
        cacheManager.getCache("recipesByCategory").evict(association.getRecipeCategory().getId());
      }
    }
  }
}
