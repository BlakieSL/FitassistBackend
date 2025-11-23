package source.code.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.helper.Enum.cache.CacheNames;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cache-stats")
public class CaffeineCacheStatsController {
    private static final List<String> CACHE_NAMES_LIST = getAllCacheNames();

    private final CacheManager cacheManager;

    public CaffeineCacheStatsController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping
    public Map<String, Object> getAllCacheStats() {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Map<String, Object>> cacheDetails = new LinkedHashMap<>();

        long totalHits = 0;
        long totalMisses = 0;
        long totalEvictions = 0;
        long totalSize = 0;

        for (String cacheName : CACHE_NAMES_LIST) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                var nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();

                totalHits += stats.hitCount();
                totalMisses += stats.missCount();
                totalEvictions += stats.evictionCount();
                totalSize += nativeCache.estimatedSize();

                Map<String, Object> cacheInfo = new LinkedHashMap<>();
                cacheInfo.put("currentSize", nativeCache.estimatedSize());
                cacheInfo.put("hitRate", stats.hitRate() * 100);
                cacheInfo.put("hitCount", stats.hitCount());
                cacheInfo.put("missCount", stats.missCount());
                cacheInfo.put("evictionCount", stats.evictionCount());

                cacheDetails.put(cacheName, cacheInfo);
            }
        }

        long totalRequests = totalHits + totalMisses;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalRequests", totalRequests);
        summary.put("totalEntriesCached", totalSize);
        summary.put("overallHitRate", totalRequests > 0 ? (totalHits * 100.0 / totalRequests) : 0.0);
        summary.put("totalHits", totalHits);
        summary.put("totalMisses", totalMisses);
        summary.put("totalEvictions", totalEvictions);

        result.put("summary", summary);
        result.put("caches", cacheDetails);

        return result;
    }

    private static List<String> getAllCacheNames() {
        List<String> cacheNames = new ArrayList<>();
        for (Field field : CacheNames.class.getDeclaredFields()) {
            try {
                cacheNames.add((String) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return cacheNames;
    }
}
