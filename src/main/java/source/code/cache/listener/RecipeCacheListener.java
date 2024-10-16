package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Recipe.RecipeCreateEvent;
import source.code.dto.request.RecipeCreateDto;

@Component
public class RecipeCacheListener {
  private final CacheManager cacheManager;

  public RecipeCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleRecipeCreate(RecipeCreateEvent event) {
    RecipeCreateDto dto = event.getRecipeCreateDto();

    cacheManager.getCache("allRecipes").clear();

    if(dto.getCategoryIds() != null) {
      for (int categoryId : dto.getCategoryIds()) {
        cacheManager.getCache("plansByCategory").evict(categoryId);
      }
    }
  }
}
