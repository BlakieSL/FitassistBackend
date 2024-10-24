package source.code.cache.event.Text;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TextCreateCacheEvent extends ApplicationEvent {
  private final String cacheKey;
  private final Object cachedData;
  public TextCreateCacheEvent(Object source, String cacheKey, Object cachedData) {
    super(source);
    this.cacheKey = cacheKey;
    this.cachedData = cachedData;
  }
}
