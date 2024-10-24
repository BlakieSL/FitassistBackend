package source.code.cache.event.Text;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TextClearCacheEvent extends ApplicationEvent {
  private final String cacheKey;
  public TextClearCacheEvent(Object source, String cacheKey) {
    super(source);
    this.cacheKey = cacheKey;
  }
}
