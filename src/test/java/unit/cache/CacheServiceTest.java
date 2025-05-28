package unit.cache;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import source.code.service.implementation.cache.CacheServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {
    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CacheServiceImpl cacheServiceImpl;

    private String cacheName;
    private String nonExistingCacheName;
    private String cacheKey;
    private String cacheData;

    @BeforeEach
    void setUp() {
        cacheName = "testCache";
        nonExistingCacheName = "nonExistentCache";
        cacheKey = "testKey";
        cacheData = "testData";
    }

    @Test
    void evictCache_shouldEvict() {
        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        cacheServiceImpl.evictCache(cacheName, cacheKey);
        verify(cache).evict(cacheKey);
    }

    @Test
    void clearCache_shouldClear() {
        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        cacheServiceImpl.clearCache(cacheName);
        verify(cache).clear();
    }

    @Test
    void putCache_shouldPut() {
        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        cacheServiceImpl.putCache(cacheName, cacheKey, cacheData);
        verify(cache).put(cacheKey, cacheData);
    }

    @Test
    void evictCache_shouldThrowExceptionWhenCacheNull() {
        when(cacheManager.getCache(nonExistingCacheName)).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.evictCache(nonExistingCacheName, cacheKey));
    }

    @Test
    void clearCache_shouldThrowExceptionWhenCacheNull() {
        when(cacheManager.getCache(nonExistingCacheName)).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.clearCache(nonExistingCacheName));
    }

    @Test
    void putCache_shouldThrowExceptionWhenCacheNull() {
        when(cacheManager.getCache(nonExistingCacheName)).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.putCache(nonExistingCacheName, cacheKey, cacheData));
    }
}
