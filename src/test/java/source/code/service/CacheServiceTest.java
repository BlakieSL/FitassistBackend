package source.code.service;

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

    @BeforeEach
    void setUp() {

    }

    @Test
    void testEvictCache() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);
        cacheServiceImpl.evictCache("testCache", "testKey");
        verify(cache).evict("testKey");
    }

    @Test
    void testClearCache() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);
        cacheServiceImpl.clearCache("testCache");
        verify(cache).clear();
    }

    @Test
    void testPutCache() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);
        cacheServiceImpl.putCache("testCache", "testKey", "testData");
        verify(cache).put("testKey", "testData");
    }

    @Test
    void testEvictCacheWithNullCache() {
        when(cacheManager.getCache("nonExistentCache")).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.evictCache("nonExistentCache", "testKey")
        );
    }

    @Test
    void testClearCacheWithNullCache() {
        when(cacheManager.getCache("nonExistentCache")).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.clearCache("nonExistentCache")
        );
    }

    @Test
    void testPutCacheWithNullCache() {
        when(cacheManager.getCache("nonExistentCache")).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                cacheServiceImpl.putCache("nonExistentCache", "testKey", "testData")
        );
    }
}
