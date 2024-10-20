package source.code.cache.event.Category;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CategoryCreateCacheEvent extends ApplicationEvent {
  private final String cacheKey;
  private final Object cachedData;
  public CategoryCreateCacheEvent(Object source, String cacheKey, Object cachedData) {
    super(source);
    this.cacheKey = cacheKey;
    this.cachedData = cachedData;
  }
}
