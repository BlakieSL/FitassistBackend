package source.code.service.implementation.cache;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import source.code.service.declaration.cache.CacheService;

import java.util.Objects;

@Service
public class CacheServiceImpl implements CacheService {

	private final CacheManager cacheManager;

	public CacheServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void evictCache(String cacheName, Object key) {
		Objects.requireNonNull(cacheManager.getCache(cacheName)).evict(key);
	}

	@Override
	public void clearCache(String cacheName) {
		Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
	}

	@Override
	public void putCache(String cacheName, Object key, Object cachedData) {
		Objects.requireNonNull(cacheManager.getCache(cacheName)).put(key, cachedData);
	}

}
