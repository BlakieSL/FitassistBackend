package source.code.event.events.Text;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TextClearCacheEvent extends ApplicationEvent {
    private final String cacheKey;

    public TextClearCacheEvent(Object source, String cacheKey) {
        super(source);
        this.cacheKey = cacheKey;
    }

    public static TextClearCacheEvent of(Object source, String cacheKey) {
        return new TextClearCacheEvent(source, cacheKey);
    }
}
