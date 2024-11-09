package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Text.TextCreateCacheEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.service.declaration.cache.CacheService;

@Component
public class TextListener {
    private final CacheService cacheService;

    public TextListener(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @EventListener
    public void handleCacheCreateEvent(TextCreateCacheEvent event) {
        cacheService.putCache(CacheNames.ALL_TEXT_BY_PARENT, event.getCacheKey(), event.getCachedData());
    }

    @EventListener
    public void handleCacheClearEvent(CategoryClearCacheEvent event) {
        cacheService.evictCache(CacheNames.ALL_TEXT_BY_PARENT, event.getCacheKey());
    }
}
