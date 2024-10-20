package source.code.cache.event.Category;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CategoryClearCacheEvent extends ApplicationEvent {
  private final String cacheKey;
  public CategoryClearCacheEvent(Object source, String cacheKey) {
    super(source);
    this.cacheKey = cacheKey;
  }
}
